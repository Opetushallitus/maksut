(ns maksut.payment.payment-service
  (:require [clojure.core.match :refer [match]]
            [maksut.error :refer [maksut-error]]
            [clojure.walk :refer [stringify-keys]]
            [maksut.maksut.db.maksut-queries :as maksut-queries]
            [maksut.payment.payment-service-protocol :as payment-service-protocol]
            [maksut.email.email-message-handling :as email-message-handling]
            [maksut.email.email-service-protocol :as email-protocol]
            [maksut.files.file-store :as file-store]
            [maksut.config :as c]
            [maksut.audit-logger-protocol :as audit]
            [maksut.schemas.class-pred :as p]
            [maksut.util.url-encoder :refer [encode]]
            [maksut.util.translation :refer [get-translation]]
            [com.stuartsierra.component :as component]
            [taoensso.timbre :refer [error info warn]]
            [clojure.string :as str]
            [schema.core :as s]
            [buddy.core.codecs :as codecs]
            [clojure.data.json :as json]
            [clj-http.client :as client]
            [buddy.core.mac :as mac])
  (:import [java.util UUID]
           (java.time ZoneOffset ZonedDateTime)
           (java.time.format DateTimeFormatter)))

(def op-payment-redirect (audit/->operation "MaksupalveluunOhjaus"))

(def op-get-kuitti (audit/->operation "KuitinHakeminen"))

(def vat-zero 0)

(defn Lasku->AuditJson [lasku]
  (assoc
   (select-keys lasku [:order_id :first_name :last_name :email :origin :reference])
   :amount (str (:amount lasku))
   :due_date (str (:due_date lasku))))

(defn- order-state [order-id]
  (cond
    (str/ends-with? order-id "-1") :käsittely
    (str/ends-with? order-id "-2") :päätös))

(defn- create-description [language-code order-id]
  (case (order-state order-id)
    :käsittely (get-translation (keyword language-code) :kuitti/käsittely)
    :päätös (get-translation (keyword language-code) :kuitti/päätös)))

(defn- create-receipt-description [language-code order-id]
  (case (order-state order-id)
    :käsittely (get-translation (keyword language-code) :kuitti/käsittely-lr)
    :päätös (get-translation (keyword language-code) :kuitti/päätös-lr)))

(defn- generate-json-data [{:keys [callback-uri]}
                           {:keys [language-code amount order-number secret first-name last-name email]}]
  (let [query (str "?tutulocale=" (encode language-code) "&tutusecret=" (encode secret))
        callback-urls {"success" (str callback-uri "/success" query)
                       "cancel"  (str callback-uri "/cancel" query)}
        amount-in-euro-cents (* 100 amount)]
    {"stamp"        (str (UUID/randomUUID))
     ; Order reference
     "reference"    order-number
     ; Total amount in EUR cents
     "amount"       amount-in-euro-cents
     "currency"     "EUR"
     "language"     (case language-code "fi" "FI" "sv" "SV" "en" "EN")
     "items"        [{"description"   (create-description language-code order-number)
                      "units"         1
                      "unitPrice"     amount-in-euro-cents
                      "vatPercentage" vat-zero
                      "productCode"   order-number}]
     "customer"     {"email"     email
                     "firstName" first-name
                     "lastName"  last-name}
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
           "checkout-timestamp" (.format (ZonedDateTime/now)
                                         (-> (DateTimeFormatter/ofPattern "yyyy-MM-dd HH:mm:ss.SSS'Z'")
                                             (.withZone ZoneOffset/UTC)))}
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
      (not (some? lasku)) (maksut-error :invoice-notfound (str "Laskua ei löydy: " secret))
      (= (:status lasku) "overdue") (maksut-error :invoice-invalidstate-overdue (str "Lasku on erääntynyt: " secret))
      (= (:status lasku) "paid") (maksut-error :invoice-invalidstate-paid (str "Lasku on jo maksettu: " secret)))

    (when (not= (:status lasku) "active")
          (maksut-error :invoice-not-active (str "Maksua ei voi enää maksaa: " secret)))

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

(defn- handle-send-email [msg email-service email]
  (let [{:keys [subject from body]} msg]
    (info "Sending email to " subject " to " email)
    (email-protocol/send-email email-service from [email] subject body)))

(defn- handle-tutu-email-confirmation
  [email-service email locale order-id reference]
  (when-let [msg (case (order-state order-id)
                   :käsittely (email-message-handling/create-tutu-processing-email email locale reference)
                   :päätös (email-message-handling/create-tutu-decision-email email locale))]
    (handle-send-email msg email-service email)))

(defn- save-receipt
  [storage-engine contents key]
  (file-store/create-file-from-bytearray storage-engine (.getBytes contents) key))

(defn- handle-payment-receipt
  [email-service email locale first-name last-name reference timestamp-millis total-amount items storage-engine oppija-baseurl]
  (let [msg (email-message-handling/create-payment-receipt email locale first-name last-name reference timestamp-millis total-amount items oppija-baseurl)]
    (future
      (try
        (save-receipt storage-engine (:body msg) reference)
        (catch Exception e
          (warn "Could not save receipt to S3 - retrying:" reference e)
          (try
            (save-receipt storage-engine (:body msg) reference)
            (catch Exception ex
              (error "Could not save receipt to S3:" reference ex))))))
    (handle-send-email msg email-service email)))

;TODO add robustness here, maybe background-job with retry?
(defn- handle-confirmation-email
  [email-service locale checkout-amount-in-euro-cents timestamp storage-engine oppija-baseurl {:keys [order-id email origin reference first-name last-name]}]
  (case origin
    "tutu" (do
             (handle-tutu-email-confirmation email-service email locale order-id
                                             reference)
             (handle-payment-receipt email-service email locale
                                     first-name last-name
                                     order-id (* 1000 timestamp)
                                     (/ checkout-amount-in-euro-cents 100)
                                     [{:description (create-receipt-description locale order-id)
                                       :units 1
                                       :unit-price (/ checkout-amount-in-euro-cents 100)
                                       :vat vat-zero}]
                                     storage-engine oppija-baseurl))
    nil))

(defn- process-success-callback [this db email-service pt-params locale storage-engine _]
  (let [{:keys [checkout-status checkout-reference checkout-amount checkout-stamp timestamp]} pt-params
        pt-config (get-paytrail-config this)
        oppija-baseurl (get-in this [:config :oppija-baseurl])
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
                                          :created (handle-confirmation-email email-service locale (bigdec checkout-amount) timestamp storage-engine oppija-baseurl result)
                                          nil)
                                   result)
                                 (return-error :payment-failed "Maksun luominen epäonnistui"))))))

(defn- kuitti-get [_ _ storage-engine {:keys [file-key]}]
  (file-store/get-file storage-engine file-key))

(defrecord PaymentService [config audit-logger email-service db storage-engine]
  component/Lifecycle
  (start [this]
    (s/validate c/MaksutConfig config)
    (s/validate (p/extends-class-pred email-protocol/EmailServiceProtocol) email-service)
    (s/validate (p/extends-class-pred audit/AuditLoggerProtocol) audit-logger)

    (s/validate s/Str (get-in config [:payment :paytrail-config :host]))
    (s/validate s/Int (get-in config [:payment :paytrail-config :merchant-id]))
    (s/validate s/Str (get-in config [:payment :paytrail-config :merchant-secret]))

    (assoc this :config (merge (:payment config) (:urls config))))
  (stop [this]
    (assoc this
           :config nil
           ))

  payment-service-protocol/PaymentServiceProtocol
  (tutu-payment [this session params]
    (tutu-payment this db audit-logger session params))
  (process-success-callback [this params locale notify?]
    (process-success-callback this db email-service params locale storage-engine notify?))
  (get-kuitti [this session params]
    (kuitti-get this session storage-engine params)))

(defn payment-payment [config]
  (map->PaymentService config))
