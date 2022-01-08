(ns maksut.maksut.maksut-service
  (:require [maksut.error :refer [maksut-error]]
            [maksut.maksut.maksut-service-protocol :refer [MaksutServiceProtocol]]
            [maksut.maksut.db.maksut-queries :as maksut-queries]
            [maksut.api-schemas :as api-schemas]
            [maksut.config :as c]
            [clojure.string :as str]
            [com.stuartsierra.component :as component]
            [schema.core :as s]
            [ring.util.http-response :as response]
            [taoensso.timbre :as log])
  (:import [java.time LocalDate]))

;Näin koska CLJS ei tue BigDecimal/LocalDate tyyppejä
(defn Lasku->json [lasku]
      (assoc
        (select-keys lasku [:order_id :first_name :last_name])
        :secret (str (:secret lasku))
        :amount (str (:amount lasku))
        :due_date (str (:due_date lasku))
        :status (keyword (:status lasku))
        :paid_at (str (:paid_at lasku))))

(defn LaskuStatus->json [lasku]
  (assoc
   (select-keys lasku [:order_id :reference])
   :status (keyword (:status lasku))))

;api_schemas/LaskuCreate (ei sisällä gereroituja kenttiä)
(defn json->LaskuCreate [lasku]
  ;this constrain cannot be in schema-def as CLJS does not support BigDecimal
  (s/validate (s/constrained s/Str #(>= (bigdec %) 0.65M) 'valid-payment-amount) (:amount lasku))
  (assoc
   (select-keys lasku [:order-id :first-name :last-name :email :due-days :origin :reference])
   :amount (.setScale (bigdec (:amount lasku)) 2 BigDecimal/ROUND_HALF_UP)))

(defn- create [_ session db lasku-input]
        (s/validate api-schemas/LaskuCreate lasku-input)
        (let [lasku (json->LaskuCreate lasku-input)
              hkr "1.2.3.testi.muuta"
              order-id (:order-id lasku)
              current (maksut-queries/get-lasku db order-id)]

          (log/info "Lasku" lasku)
          (log/info "Current" current)
          ;TODO validate input: valid email, fields set
          ;schemassa validoidaan jo
          ;(<= (:due-days lasku) 0) (maksut-error :invoice-createerror-invalidduedays "Eräpäivien lukumäärä ei ole sallittu")
          ;(<= (.compareTo (:amount lasku) 0M) 0) (maksut-error :invoice-createerror-invalidamount "Laskun summa ei ole sallittu")))

          ; Why is the date 3 hours off (is DB in UTC Timezone?)

          (maksut-queries/create-or-update-lasku db lasku)
          ;returns created/changed fields from view (including generated fields)
          ;TODO tarvitaan secret mukaan
          (Lasku->json (maksut-queries/get-lasku-by-order-id db {:order-id order-id}))))

(defrecord MaksutService [audit-logger config db]
  component/Lifecycle
  (start [this]
    (s/validate c/MaksutConfig config)
    (assoc this :config (select-keys (:tutu config) [:lasku-origin
                                                     :order-id-prefix])))
  (stop [this]
    (assoc this :config nil))

  MaksutServiceProtocol

  (create [this session lasku]
    (create this session db lasku))

  (create-tutu [this session lasku]
    (s/validate api-schemas/TutuLaskuCreate lasku)
    (let [{:keys [application-key index]} lasku
          trim-zeroes (fn this [str] (if (clojure.string/starts-with? str "0")
                        (this (subs str 1))
                        str))
          ;Using the last part of application-key OID as unique order-id
          aid (trim-zeroes (last (str/split application-key #"[.]")))
          prefix (get-in this [:config :order-id-prefix])
          order-id (str prefix aid "-" index)]
      (create this session db
              (assoc
                (select-keys lasku [:first-name :last-name :email :amount])
                :order-id order-id
                :due-days 14  ;TODO fetch this from config
                :origin (get-in this [:config :lasku-origin])
                :reference application-key))))

  (list-tutu [this session input]
    (let [{:keys [application-key index]} input
          origin (get-in this [:config :lasku-origin])]
      ;TODO handle index if needed (by ataru-editori use-cases)
      (s/validate s/Str application-key)
      (if-let [laskut (seq (maksut-queries/get-laskut-by-reference db origin application-key))]
        ;TODO secret should not maybe be included here if it's not needed (only return it when asked with secret, or when new invoice is created?)
        (map Lasku->json laskut)
        (maksut-error :invoice-notfound "Laskuja ei löytynyt"))))

  (check-status-tutu [this session input]
    (let [origin (get-in this [:config :lasku-origin])
          keys (:keys input)]
      (let [statuses (maksut-queries/check-laskut-statuses-by-reference db origin keys)]
        (map LaskuStatus->json statuses)
        )))

  (get-lasku [_ session order-id]
    (if-let [lasku (maksut-queries/get-lasku-by-order-id db {:order-id order-id})]
      ; Mikäli lasku tulee maksetuksi manuaalisesti (Paytrailin ulkopuolella), se tulee näkymään "past-due"
      ; täällä ja se merkataan maksetuksi virkailijan toimesta suoraan Ataru-lomakkeeseen

      (Lasku->json lasku)
      (response/not-found! "Lasku not found")))

  (get-laskut-by-secret [_ session secret]
    (if-let [laskut (seq (maksut-queries/get-laskut-by-secret db secret))]
      (let [now (. LocalDate (now))
            passed? #(.isAfter now %)
            all-passed? (every? passed? (mapv :due_date laskut))]
        ;do not let user to the page if all due_dates for all (linked) invoices has passed
        (log/info "laskut " laskut)
        (if all-passed?
          (maksut-error :invoice-notfound-oldsecret "Linkki on vanhentunut")
          (map Lasku->json laskut)))
      (maksut-error :invoice-notfound-secret "Linkki on väärä tai vanhentunut"))))


