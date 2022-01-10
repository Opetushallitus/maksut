(ns maksut.payment.payment-service
  (:require ;[oti.boundary.db-access :as dba]
            ;[oti.boundary.payment :as payment-util]
            ;[clojure.tools.logging :refer [error info]]
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
            [schema.core :as s]
            ;[oti.service.user-data :as user-data]
            ;[oti.service.registration :as registration]
            ;[oti.util.logging.audit :as audit]
            ;[oti.db-states :as states]
            ;[oti.boundary.api-client-access :as api]
            )
  (:import [java.time LocalDateTime]
           [java.time.format DateTimeFormatter]
           [java.util Locale]
           [org.apache.commons.codec.digest DigestUtils]))

(defn- blank->nil [s]
  (when-not (str/blank? s)
    s))

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


(defn- process-response! [{:keys [paytrail-payment db]} form-data db-fn]
  (let [db-params {:order-number (:ORDER_NUMBER form-data)
                   :pay-id (blank->nil (:PAYMENT_ID form-data))
                   :payment-method (blank->nil (:PAYMENT_METHOD form-data))}]
    (if (and (return-authcode-valid? paytrail-payment form-data) (:order-number db-params))
      (do (db-fn db db-params)
          true)
      (error "Could not verify payment response message:" form-data))))

(defn- send-confirmation-email! [config {:keys [ORDER_NUMBER] :as payment-data} lang]
  (if (not-any? str/blank? [ORDER_NUMBER])
    (prn "send-confirmation-email!")
    ;(some->> (user-data/participant-data config ORDER_NUMBER lang)
    ;         (registration/send-confirmation-email! config lang))
    (error "Can't send confirmation email because of missing data. Payment data:" payment-data)))

;(defn confirm-payment! [config form-data lang]
;  (when (process-response! config form-data prn);dba/confirm-registration-and-payment!)
;    ;    (audit/log :app :admin
;    ;               :on :payment
;    ;               :op :update
;    ;               :id (:ORDER_NUMBER form-data)
;    ;               :before {:state states/pmt-unpaid}
;    ;               :after {:state states/pmt-ok}
;    ;               :msg "Payment has been confirmed.")
;    (try
;      (send-confirmation-email! config form-data lang)
;      (catch Throwable t
;        (error t "Could not send confirmation email. Payment data:" form-data)))
;    :confirmed))

;(defn cancel-payment! [config form-data]
;  (when (process-response! config form-data prn) ;dba/cancel-registration-and-payment!)
;    ;    (audit/log :app :admin
;    ;               :on :payment
;    ;               :op :update
;    ;               :id (:ORDER_NUMBER form-data)
;    ;               :before {:state states/pmt-unpaid}
;    ;               :after {:state states/pmt-error}
;    ;               :msg "Payment has been cancelled.")
;    :cancelled))

;(defn- cancel-payment-by-order-number! [db {:keys [state order_number]}]
;    ;  (audit/log :app :admin
;    ;             :on :payment
;    ;             :op :update
;    ;             :id order_number
;    ;             :before {:state state}
;    ;             :after {:state states/pmt-error}
;    ;             :msg "Payment has been cancelled.")
;  ;(dba/cancel-registration-and-payment! db {:order-number order_number})
;  :cancelled)

;(defn confirm-payment-manually! [{:keys [db] :as config} order-number user-lang session]
;  {:pre [order-number user-lang]}
;    ;  (audit/log :app :admin
;    ;             :who (get-in session [:identity :oid])
;    ;             :ip (get-in session [:identity :ip])
;    ;             :user-agent (get-in session [:identity :user-agent])
;    ;             :on :payment
;    ;             :op :update
;    ;             :id order-number
;    ;             :before {:state states/pmt-error}
;    ;             :after {:state states/pmt-ok}
;    ;             :msg "Payment and related registration has been approved.")
;  (when (= 1 1;(dba/confirm-registration-and-payment! db {:order-number order-number})
;           )
;    ;    (some->> (user-data/participant-data config order-number user-lang)
;    ;             (registration/send-confirmation-email! config user-lang))
;    true))

;(defn cancel-obsolete-payments! [db]
;  (info "Cancelled obsolete payments" ;(dba/cancel-obsolete-registrations-and-payments! db)
;        ))

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
;  {:pre  [(s/valid? ::os/pt-payment-params params)]
;   :post [(s/valid? ::os/pt-payment-form-data %)]}
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
        pt-config (get-paytrail-config this)]
    (cond
      (not (return-authcode-valid? pt-config pt-params)) (maksut-error :payment-invalid-status "Maksun tiedoissa on vikaa")
      (not= STATUS "PAID") (maksut-error :payment-invalid-status "Maksun tiedoissa on vikaa"))

    ;TODO only send email once, but if 1st success was a failure, then notify? should be able to send the email also
    ;TODO add some check for due-date, with maybe 1day grace-period (but basically user should not be able to initiate Paytrail payment themselves)

    (when-let [result (maksut-queries/create-payment db pt-params)]
      (handle-confirmation-email email-service locale result)
      result)))


(defrecord PaymentService [config audit-logger email-service db]
  component/Lifecycle
  (start [this]
    ;(s/validate (s/pred #(instance? DataSource %)) (:datasource db))
    (s/validate c/MaksutConfig config)
    (s/validate (p/extends-class-pred email-protocol/EmailServiceProtocol) email-service)
    (s/validate (p/extends-class-pred audit/AuditLoggerProtocol) audit-logger)

    ;(s/validate s/Str (get-in config [:payment :paytrail-config :host]))
    ;(s/validate s/Int (get-in config [:payment :paytrail-config :merchant-id]))

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
