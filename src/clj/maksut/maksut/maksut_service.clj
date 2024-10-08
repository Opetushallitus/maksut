(ns maksut.maksut.maksut-service
  (:require [clojure.core.match :refer [match]]
            [maksut.error :refer [maksut-error]]
            [maksut.maksut.maksut-service-protocol :refer [MaksutServiceProtocol]]
            [maksut.maksut.db.maksut-queries :as maksut-queries]
            [maksut.api-schemas :as api-schemas]
            [maksut.util.date :refer [iso-date-str->date]]
            [clojure.string :as str]
            [com.stuartsierra.component :as component]
            [clj-time.core :as time]
            [schema.core :as s]
            [ring.util.http-response :as response]
            [taoensso.timbre :as log]
            [camel-snake-kebab.core :as csk]
            [camel-snake-kebab.extras :as cske])
  (:import (java.math RoundingMode)
           [java.time LocalDate]))

;Näin koska CLJS ei tue BigDecimal/LocalDate tyyppejä
(defn Lasku->json [lasku]
      (cond->
        (select-keys lasku [:order_id :first_name :last_name :origin :reference])
        true
        (assoc
          :secret (str (:secret lasku))
          :amount (str (:amount lasku))
          :due_date (str (:due_date lasku))
          :status (keyword (:status lasku))
          :paid_at (str (:paid_at lasku)))
        (not-empty (:metadata lasku))
        (assoc
          :metadata (:metadata lasku))))

(defn LaskuStatus->json [lasku]
  (assoc
   (select-keys lasku [:order_id :reference :origin])
   :status (keyword (:status lasku))))

;api_schemas/LaskuCreate (ei sisällä gereroituja kenttiä)
(defn json->LaskuCreate [lasku]
  ;this constrain cannot be in schema-def as CLJS does not support BigDecimal
  (s/validate (s/constrained s/Str #(>= (bigdec %) 0.65M) 'valid-payment-amount) (:amount lasku))
  (assoc
   (select-keys lasku [:order-id :first-name :last-name :email :due-days :origin :reference])
    :due-date (or
               (iso-date-str->date (:due-date lasku))
               (time/plus (time/today) (time/days (:due-days lasku))))
    :amount (.setScale (bigdec (:amount lasku)) 2 RoundingMode/HALF_UP)
    :metadata (cske/transform-keys
                (csk/->snake_case)
                (or (:metadata lasku) {}))))

(defn- parse-order-id [prefixes lasku]
  (let [trim-zeroes (fn this [str] (if (clojure.string/starts-with? str "0")
                                     (this (subs str 1))
                                     str))
        ;Using the last part of application-key OID as unique order-id
        aid (trim-zeroes (last (str/split (:reference lasku) #"[.]")))
        origin (:origin lasku)
        prefix ((keyword origin) prefixes)
        suffix (if (some? (:index lasku))
                 (str "-" (:index lasku))
                 "")]
    (str prefix aid suffix)))

(defn- create [this _ db lasku-input]
  (s/validate api-schemas/LaskuCreate lasku-input)
  (let [order-id (or (:order-id lasku-input)
                     (parse-order-id
                       (get-in this [:config :payment :order-id-prefix])
                       lasku-input))
        lasku (assoc
                (json->LaskuCreate lasku-input)
                :order-id order-id)
        {:keys [order-id due-date]} lasku]

    (log/info "Lasku" lasku)
    (log/info "Current" (maksut-queries/get-lasku db order-id))

    (when-not (time/before? (time/today) due-date)
      (maksut-error :invoice-createerror-duedateinpast (str "Due-date needs to be in future: " lasku) :status-code 422))

    (maksut-queries/create-or-update-lasku db lasku)
    ;returns created/changed fields from view (including generated fields)
    (Lasku->json (maksut-queries/get-lasku-by-order-id db {:order-id order-id}))))

(defn- throw-specific-old-secret-error [laskut secret]
  (let [order-id-matcher #(first (filter (fn [x] (str/ends-with? (:order_id x) %)) laskut))
        processing (order-id-matcher "-1")
        decision (order-id-matcher "-2")
        output #(maksut-error % (str "Linkki on vanhentunut: " secret))]
    (match [(:status processing) (:status decision)]
           ["paid"    nil] (output :invoice-processing-oldsecret)
           ["overdue" nil] (output :invoice-processing-overdue)
           [_ "paid"]      (output :invoice-decision-oldsecret)
           [_ "overdue"]   (output :invoice-decision-overdue)
           :else (maksut-error :invoice-notfound-oldsecret (str "Linkki on vanhentunut: " secret)))))

(defrecord MaksutService [audit-logger config db]
  component/Lifecycle
  (start [this]
    (assoc this :config config))
  (stop [this]
    (assoc this :config nil))

  MaksutServiceProtocol

  (create [this session lasku]
    (create this session db lasku))

  (create-tutu [this session lasku]
    (s/validate api-schemas/TutuLaskuCreate lasku)
    (let [{:keys [application-key _ index]} lasku
          trim-zeroes (fn this [str] (if (clojure.string/starts-with? str "0")
                        (this (subs str 1))
                        str))
          ;Using the last part of application-key OID as unique order-id
          aid (trim-zeroes (last (str/split application-key #"[.]")))
          prefix (get-in this [:config :tutu :order-id-prefix])
          order-id (str prefix aid "-" index)]
      (create this session db
              (assoc
                (select-keys lasku [:first-name :last-name :email :amount :due-date])
                :order-id order-id
                :due-days 14 ;if due-date not defined
                :origin (get-in this [:config :tutu :lasku-origin])
                :reference application-key))))

  (list-laskut [_ _ input]
    (let [{:keys [application-key]} input]
      (s/validate s/Str application-key)
      (if-let [laskut (seq (maksut-queries/get-laskut-by-reference db application-key))]
        (map Lasku->json laskut)
        (do (log/warn (str "Laskuja ei löytynyt hakemusavaimella " application-key))
            []))))

  (check-status [_ _ input]
    (let [{:keys [keys]} input
          statuses (maksut-queries/check-laskut-statuses-by-reference db keys)]
      (map LaskuStatus->json statuses)))

  (get-lasku [_ _ order-id]
    (if-let [lasku (maksut-queries/get-lasku-by-order-id db {:order-id order-id})]
      ; Mikäli lasku tulee maksetuksi manuaalisesti (Paytrailin ulkopuolella), se tulee näkymään "past-due"
      ; täällä ja se merkataan maksetuksi virkailijan toimesta suoraan Ataru-lomakkeeseen

      (Lasku->json lasku)
      (response/not-found! "Lasku not found")))

  (get-laskut-by-secret [_ _ secret]
    (if-let [laskut (seq (maksut-queries/get-laskut-by-secret db secret))]
      (let [now (. LocalDate (now))
            passed? #(.isAfter now %)
            all-passed? (every? passed? (mapv :due_date laskut))]
        ;do not let user to the page if all due_dates for all (linked) invoices has passed
        (if all-passed?
          (throw-specific-old-secret-error laskut secret)
          (map Lasku->json laskut)))
      (do (log/error (str "Linkki on väärä tai vanhentunut: " secret))
          (maksut-error :invoice-notfound-secret (str "Linkki on väärä tai vanhentunut: " secret))))))


