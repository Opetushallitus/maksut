(ns maksut.payment.payment-service
  (:require [clojure.core.match :refer [match]]
            [maksut.error :refer [maksut-error]]
            [maksut.api-schemas :as api-schemas]
            [maksut.maksut.db.maksut-queries :as maksut-queries]
            [maksut.payment.payment-service-protocol :as payment-service-protocol]
            [maksut.email.tutu-payment-confirmation :as email-confirmation]
            [maksut.email.email-service-protocol :as email-protocol]
            [maksut.config :as c]
            [maksut.audit-logger-protocol :as audit]
            [maksut.schemas.class-pred :as p]
            [maksut.util.url-encoder :refer [encode]]
            [re-frame.core :refer [dispatch]]
            [com.stuartsierra.component :as component]
            [taoensso.timbre :refer [error info]]
            [clojure.string :as str]
            [schema.core :as s])
  (:import [java.time LocalDateTime]
           [java.time.format DateTimeFormatter]
           [java.util Locale]
           [org.apache.commons.codec.digest DigestUtils]))

(def op-payment-redirect (audit/->operation "MaksupalveluunOhjaus"))

(defn Lasku->AuditJson [lasku]
  (assoc
   (select-keys lasku [:order_id :first_name :last_name :email :origin :reference])
   :amount (str (:amount lasku))
   :due_date (str (:due_date lasku))))


(def response-keys [:ORDER_NUMBER :PAYMENT_ID :AMOUNT :TIMESTAMP :STATUS])

(defn- return-authcode-valid? [{:keys [merchant-secret]} form-data]
  (if-let [return-authcode (:RETURN_AUTHCODE form-data)]
    (let [plaintext (-> (->> response-keys
                             (map #(% form-data))
                             (remove nil?)
                             (str/join "|"))
                        (str "|" merchant-secret))
          calculated-authcode (-> plaintext DigestUtils/sha256Hex str/upper-case)]
      (= return-authcode calculated-authcode))
    (error "Tried to authenticate message, but the map contained no :RETURN_AUTHCODE key. Data:" form-data)))

; -----

(defn- format-number-us-locale [n]
  (String/format (Locale. "us"), "%.2f", (to-array [(double n)])))

(defn- calculate-authcode [{:keys [MERCHANT_ID LOCALE URL_SUCCESS URL_CANCEL URL_NOTIFY
                                   AMOUNT ORDER_NUMBER PARAMS_IN PARAMS_OUT]} secret]
  (let [plaintext (str/join "|" (->> [secret MERCHANT_ID LOCALE URL_SUCCESS URL_CANCEL URL_NOTIFY
                                      AMOUNT ORDER_NUMBER PARAMS_IN PARAMS_OUT]
                                     (remove nil?)))]
    (-> plaintext (.getBytes "ISO-8859-1") DigestUtils/sha256Hex str/upper-case)))

(defn- generate-form-data [{:keys [paytrail-host callback-uri merchant-id merchant-secret]}
                           {:keys [language-code amount order-number secret reference-number msg] :as params}]
  ;Paytrail does not support sending back the LOCALE we sent, so need redundant field for that
  (let [params-in "MERCHANT_ID,LOCALE,URL_SUCCESS,URL_CANCEL,URL_NOTIFY,AMOUNT,ORDER_NUMBER,PARAMS_IN,PARAMS_OUT"
        params-out "ORDER_NUMBER,PAYMENT_ID,AMOUNT,TIMESTAMP,STATUS"
        query (str "?tutulocale=" (encode language-code) "&tutusecret=" (encode secret))
        form-params {:MERCHANT_ID  merchant-id
                     :LOCALE       (case language-code "fi" "fi_FI" "sv" "sv_SE" "en" "en_US")
                     :URL_SUCCESS  (str callback-uri "/success" query)
                     :URL_CANCEL   (str callback-uri "/cancel" query)
                     :URL_NOTIFY   (str callback-uri "/notify" query)
                     :AMOUNT       (format-number-us-locale amount)
                     :ORDER_NUMBER order-number
                     :PARAMS_IN params-in
                     :PARAMS_OUT params-out}
        authcode (calculate-authcode form-params merchant-secret)]
    {:uri    paytrail-host
     :params (assoc form-params :AUTHCODE authcode)}))

(defn- get-paytrail-config [this]
  (let [config (:config this)]
    {:paytrail-host   (-> config :paytrail-config :host)
     :merchant-id     (-> config :paytrail-config :merchant-id)
     :merchant-secret (-> config :paytrail-config :merchant-secret)
     :callback-uri    (-> config :callback-uri)}))

;Instead of directly searching by order-id, use secret to prevent order-id brute-forcing
(defn- tutu-payment [this db audit-logger session {:keys [order-id locale secret]}]
  (let [laskut (maksut-queries/get-laskut-by-secret db secret)
        lasku (first (filter (fn [x] (= (:order_id x) order-id)) laskut))
        lang (or locale "fi")
        p {:language-code    lang
           :amount           (:amount lasku)
           :order-number     order-id
           :secret           secret
           }]
    (cond
      (not (some? lasku)) (maksut-error :invoice-notfound "Laskua ei löydy")
      (= (:status lasku) "overdue") (maksut-error :invoice-invalidstate-overdue "Lasku on erääntynyt")
      (= (:status lasku) "paid") (maksut-error :invoice-invalidstate-paid "Lasku on jo maksettu"))

    (when (not= (:status lasku) "active")
          (maksut-error :invoice-not-active "Maksua ei voi enää maksaa"))

    (let [data (generate-form-data (get-paytrail-config this) p)
          audit-data (Lasku->AuditJson lasku)]

      (audit/log audit-logger
                 (audit/->user session)
                 op-payment-redirect
                 (audit/->target {:email (:email lasku)})
                 (audit/->changes {} audit-data))

      data)))


(defn- handle-tutu-confirmation-email [email-service email locale order-id reference]
  (when-let [msg (cond
                   (str/ends-with? order-id "-1") (email-confirmation/create-processing-email email locale reference)
                   (str/ends-with? order-id "-2") (email-confirmation/create-decision-email email locale))]
    (let [{:keys [subject from body]} msg]
      (info "Sending email to " subject " to " email)
      (email-protocol/send-email email-service from [email] subject body))))

;TODO add robustness here, maybe background-job with retry?
(defn- handle-confirmation-email [email-service locale {:keys [action order-id email origin reference]}]
  (case origin
    "tutu" (handle-tutu-confirmation-email email-service email locale order-id reference)
    nil))

(defn- process-success-callback [this db email-service pt-params locale notify?]
  (s/validate api-schemas/PaytrailCallbackRequest pt-params)

  (let [{:keys [STATUS]} pt-params
        pt-config (get-paytrail-config this)
        return-error (fn [code msg]
                       (error (str "Payment handling error " code " " msg " " pt-params))
                       {:action :error
                        :code code})]

    ;due-date is not checked here again, as it might take up to 5-7 days for Paytrail to
    ;manually process payments where the first redirect-callback was skipped

    (let [auth-ok (return-authcode-valid? pt-config pt-params)
          status-ok (= STATUS "PAID")]
      (match [auth-ok status-ok]
             [false _] (return-error :payment-invalid-status "Maksun tiedoissa on vikaa")
             [_ false] (return-error :payment-invalid-status "Maksun tiedoissa on virhe")
             [true true] (if-let [result (maksut-queries/create-payment db pt-params)]
                                 (do
                                   (case (:action result)
                                          :created (handle-confirmation-email email-service locale result)
                                          nil)
                                   result)
                                 (return-error :payment-failed "Maksun luominen epäonnistui"))
             ))))

(defrecord PaymentService [config audit-logger email-service db]
  component/Lifecycle
  (start [this]
    (s/validate c/MaksutConfig config)
    (s/validate (p/extends-class-pred email-protocol/EmailServiceProtocol) email-service)
    (s/validate (p/extends-class-pred audit/AuditLoggerProtocol) audit-logger)

    (s/validate s/Str (get-in config [:payment :paytrail-config :host]))
    (s/validate s/Int (get-in config [:payment :paytrail-config :merchant-id]))
    (s/validate s/Str (get-in config [:payment :paytrail-config :merchant-secret]))

    (assoc this :config (:payment config)))
  (stop [this]
    (assoc this
           :config nil
           ))

  payment-service-protocol/PaymentServiceProtocol
  (tutu-payment [this session params]
    (tutu-payment this db audit-logger session params))
  (process-success-callback [this params locale notify?]
    (process-success-callback this db email-service params locale notify?))
  (form-data-for-payment [this params]
    (generate-form-data this params))
  (authentic-response? [this form-data]
    (return-authcode-valid? this form-data)))

(defn payment-payment [config]
  (map->PaymentService config))
