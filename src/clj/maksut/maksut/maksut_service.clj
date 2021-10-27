(ns maksut.maksut.maksut-service
  (:require [maksut.audit-logger-protocol :as audit]
            [maksut.error :refer [maksut-error]]
            [maksut.maksut.maksut-service-protocol :refer [MaksutServiceProtocol]]
            [maksut.maksut.db.maksut-queries :as maksut-queries]
            [maksut.api-schemas :as api-schemas]
            [maksut.config :as c]
            [clojure.string :as str]
            [com.stuartsierra.component :as component]
            [schema.core :as s]
            [ring.util.http-response :as response]
            [taoensso.timbre :as log]))

(def maksut-create (audit/->operation "MaksutCreate"))

;(defn log-return [arg]
;            (log/info "LOGGED " (pr-str arg))
;            arg)

;Näin koska CLJS ei tue BigDecimal/LocalDate tyyppejä
(defn Lasku->json [lasku]
      (assoc
        (select-keys lasku [:order_id :first_name :last_name])
        :secret (str (:secret lasku))
        :amount (str (:amount lasku))
        :due_date (str (:due_date lasku))
        :status (keyword (:status lasku))
        :paid_at (str (:paid_at lasku))))

;api_schemas/LaskuCreate (ei sisällä gereroituja kenttiä)
(defn json->LaskuCreate [lasku]
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

          ;(audit/log audit-logger
          ;           (audit/->user session)
          ;           maksut-create
          ;           (audit/->target {:oid (:oid hkr)})
          ;           (audit/->changes {} hkr))

          (maksut-queries/create-or-update-lasku db lasku)
          ;returns created/changed fields from view (including generated fields)
          ;TODO tarvitaan secret mukaan
          (Lasku->json (maksut-queries/get-lasku-by-order-id db {:order-id order-id}))))

(defrecord MaksutService [audit-logger config db]
  component/Lifecycle
  (start [this]
    (s/validate c/MaksutConfig config)
    (assoc this :config (select-keys (:payment config) [:lasku-origin
                                                        :order-id-prefix])))
  (stop [this]
    (assoc this :config nil))

  MaksutServiceProtocol

  (create [this session lasku]
    (create this session db lasku))

  (create-tutu [this session lasku]
    (s/validate api-schemas/TutuLaskuCreate lasku)
    (let [{:keys [application-id index]} lasku
          ;Using the last part of application-id OID as unique order-id
          aid (last (str/split application-id #"[.]"))
          prefix (get-in this [:config :order-id-prefix])
          order-id (str prefix aid "-" index)]
      (create this session db
              (assoc
                (select-keys lasku [:first-name :last-name :email :amount])
                :order-id order-id
                :due-days 14  ;TODO fetch this from config
                :origin (get-in this [:config :lasku-origin])
                :reference application-id))))

  (get-lasku [_ session order-id]
    (if-let [lasku (maksut-queries/get-lasku-by-order-id db {:order-id order-id})]
      ; Mikäli lasku tulee maksetuksi manuaalisesti (Paytrailin ulkopuolella), se tulee näkymään "past-due"
      ; täällä ja se merkataan maksetuksi virkailijan toimesta suoraan Ataru-lomakkeeseen

      (Lasku->json lasku)
      (response/not-found! "Lasku not found")))

  (get-laskut-by-secret [_ session secret]
    (if-let [laskut (seq (maksut-queries/get-laskut-by-secret db secret))]
      (do
        (log/info "laskut " laskut)
        (map Lasku->json laskut))
      (maksut-error :invoice-notfound-secret "Linkki on väärä tai vanhentunut"))))


