(ns maksut.payment.payment-service
  (:require [clojure.core.match :refer [match]]
            [maksut.error :refer [maksut-error]]
            [clojure.walk :refer [stringify-keys]]
            [maksut.maksut.db.maksut-queries :as maksut-queries]
            [maksut.payment.payment-service-protocol :as payment-service-protocol]
            [maksut.email.tutu-payment-confirmation :as email-confirmation]
            [maksut.email.email-service-protocol :as email-protocol]
            [maksut.config :as c]
            [maksut.audit-logger-protocol :as audit]
            [maksut.schemas.class-pred :as p]
            [maksut.util.url-encoder :refer [encode]]
            [com.stuartsierra.component :as component]
            [taoensso.timbre :refer [error info]]
            [clojure.string :as str]
            [clj-time.core :as time]
            [schema.core :as s]
            [buddy.core.codecs :as codecs]
            [clojure.data.json :as json]
            [clj-http.client :as client]
            [buddy.core.mac :as mac])
  (:import [java.util UUID]))

(def op-payment-redirect (audit/->operation "MaksupalveluunOhjaus"))

(defn Lasku->AuditJson [lasku]
  (assoc
   (select-keys lasku [:order_id :first_name :last_name :email :origin :reference])
   :amount (str (:amount lasku))
   :due_date (str (:due_date lasku))))

(defn- generate-json-data [{:keys [callback-uri]}
                           {:keys [language-code amount order-number secret first-name last-name email]}]
  (let [query (str "?tutulocale=" (encode language-code) "&tutusecret=" (encode secret))
        callback-urls {"success" (str callback-uri "/success" query)
                       "cancel"  (str callback-uri "/cancel" query)}]
    {"stamp"        (str (UUID/randomUUID))
     ; Order reference
     "reference"    order-number
     ; Total amount in EUR cents
     "amount"       amount ; (format-number-us-locale amount)
     "currency"     "EUR"
     "language"     (case language-code "fi" "FI" "sv" "SV" "en" "EN")
     "customer"     {"email"     email
                     "firstName" (or first-name "testi")
                     "lastName"  (or last-name "tester")}
     "redirectUrls" callback-urls
     "callbackUrls" callback-urls
     }))

(defn- get-paytrail-config [this]
  (let [config (:config this)]
    {:paytrail-host   (-> config :paytrail-config :host)
     :merchant-id     (-> config :paytrail-config :merchant-id)
     :merchant-secret (-> config :paytrail-config :merchant-secret)
     :callback-uri    (-> config :callback-uri)}))

(defn- authentication-headers [method merchant-id transaction-id]
  (cond-> {"checkout-account"   merchant-id
           "checkout-algorithm" "sha512"
           "checkout-method"    method
           "checkout-nonce"     (str (UUID/randomUUID))
           "checkout-timestamp" (str (time/now))}
          (some? transaction-id)
          (assoc "checkout-transaction-id" transaction-id)))

(defn- headers->signature-keys-order [headers]
  (->> (keys headers)
       (filter #(str/starts-with? % "checkout-"))
       (sort)))

(defn- sign-request [merchant-secret headers body]
  (let [sb (StringBuilder.)]
    (doseq [header (headers->signature-keys-order headers)]
      (when-let [data (headers header)]
        (.append sb header)
        (.append sb ":")
        (.append sb data)
        (.append sb "\n")))
    (when body
      (.append sb body))
    (-> (.toString sb)
        (mac/hash {:key merchant-secret
                   :alg :hmac+sha512})
        (codecs/bytes->hex))))

(defn- lasku-to-json [lasku paytrail-config order-id locale secret]
  (let [p {:language-code    (or locale "fi")
       :amount           (:amount lasku)
       :order-number     order-id
       :secret           secret
       :first-name       (:first_name lasku)
       :last-name        (:last_name lasku)
       :email            (:email lasku)}]
    (json/write-str (generate-json-data paytrail-config p))))

;Instead of directly searching by order-id, use secret to prevent order-id brute-forcing
(defn- tutu-payment [this db audit-logger session {:keys [order-id locale secret]}]
  (let [laskut (maksut-queries/get-laskut-by-secret db secret)
        lasku (first (filter (fn [x] (= (:order_id x) order-id)) laskut))]
    (cond
      (not (some? lasku)) (maksut-error :invoice-notfound "Laskua ei löydy")
      (= (:status lasku) "overdue") (maksut-error :invoice-invalidstate-overdue "Lasku on erääntynyt")
      (= (:status lasku) "paid") (maksut-error :invoice-invalidstate-paid "Lasku on jo maksettu"))

    (when (not= (:status lasku) "active")
          (maksut-error :invoice-not-active "Maksua ei voi enää maksaa"))

    (let [paytrail-config (get-paytrail-config this)
          paytrail-host (:paytrail-host paytrail-config)
          merchant-id (:merchant-id paytrail-config)
          merchant-secret (:merchant-secret paytrail-config)
          authentication-headers (authentication-headers "POST" merchant-id nil)
          body (lasku-to-json lasku paytrail-config order-id locale secret)
          response (-> {:method           :post
                        :url              paytrail-host
                        :content-type     "application/json; charset=utf-8"
                        :throw-exceptions true
                        :as               :json
                        :headers          (-> authentication-headers
                                              (assoc "signature" (sign-request merchant-secret authentication-headers body)))
                        :body             body}
                       client/request)
          audit-data (Lasku->AuditJson lasku)]

      (audit/log audit-logger
                 (audit/->user session)
                 op-payment-redirect
                 (audit/->target {:email (:email lasku)})
                 (audit/->changes {} audit-data))

      (-> response :body))))


(defn- handle-tutu-confirmation-email [email-service email locale order-id reference]
  (when-let [msg (cond
                   (str/ends-with? order-id "-1") (email-confirmation/create-processing-email email locale reference)
                   (str/ends-with? order-id "-2") (email-confirmation/create-decision-email email locale))]
    (let [{:keys [subject from body]} msg]
      (info "Sending email to " subject " to " email)
      (email-protocol/send-email email-service from [email] subject body))))

;TODO add robustness here, maybe background-job with retry?
(defn- handle-confirmation-email [email-service locale {:keys [order-id email origin reference]}]
  (case origin
    "tutu" (handle-tutu-confirmation-email email-service email locale order-id reference)
    nil))

(defn- process-success-callback [this db email-service pt-params locale _]
  ;(s/validate api-schemas/PaytrailCallbackRequest pt-params)
  (let [{:keys [checkout-status checkout-reference checkout-amount checkout-stamp timestamp]} pt-params
        pt-config (get-paytrail-config this)
        signed-headers (sign-request (:merchant-secret pt-config) (stringify-keys pt-params) nil)
        return-error (fn [code msg]
                       (error (str "Payment handling error " code " " msg " " pt-params))
                       {:action :error
                        :code code})]

    ;due-date is not checked here again, as it might take up to 5-7 days for Paytrail to
    ;manually process payments where the first redirect-callback was skipped
    (info "Processing success callback")
    (let [auth-ok (= signed-headers (:signature pt-params))
          status-ok (= checkout-status "ok")]
      (match [auth-ok status-ok]
             [false _] (return-error :payment-invalid-status "Maksun tiedoissa on vikaa")
             [_ false] (return-error :payment-invalid-status "Maksun tiedoissa on virhe")
             [true true] (if-let [result (maksut-queries/create-payment db checkout-reference checkout-stamp checkout-amount timestamp)]
                                 (do
                                   (case (:action result)
                                          :created (handle-confirmation-email email-service locale result)
                                          nil)
                                   result)
                                 (return-error :payment-failed "Maksun luominen epäonnistui"))))))

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
    (process-success-callback this db email-service params locale notify?)))

(defn payment-payment [config]
  (map->PaymentService config))
