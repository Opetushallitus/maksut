(ns maksut.maksut.db.maksut-queries
  (:import [org.joda.time LocalDate])
  (:require [maksut.error :refer [maksut-error]]
            [hugsql.core :as hugsql]
            [clojure.java.jdbc :refer [with-db-transaction]]
            [clojure.data :refer [diff]]
            [taoensso.timbre :as log]))


(hugsql/def-db-fns "maksut/maksut/db/maksut_queries.sql")

;; Esittele sql-kyselyt
(declare insert-lasku-returning!)
(declare update-lasku!)
(declare get-lasku-by-order-id)
(declare all-linked-laskut-by-secret)
(declare get-lasku-locked)
(declare select-payment)
(declare insert-payment!)

(def initial-settings
  {
;   :rajaava false
;   :max-hakukohteet nil
;   :jos-ylioppilastutkinto-ei-muita-pohjakoulutusliitepyyntoja false
;   :yo-amm-autom-hakukelpoisuus false
   })

;(defn find-settings-by-hakukohderyhma-oids
;  [db hakukohderyhma-oids]
;  (let [settings (->> {:hakukohderyhma-oids hakukohderyhma-oids}
;                      (settings-by-hakukohderyhma-oids db)
;                      (group-by :hakukohderyhma-oid))]
;    (->> hakukohderyhma-oids
;         (map (fn [hakukohderyhma-oid]
;                (if-let [matching-settings (get settings hakukohderyhma-oid)]
;                  (first matching-settings)
;                  initial-settings))))))

(defn has-changed? [old new]
  (or
   (not= (:first-name new) (:first_name old))
   (not= (:last-name new) (:last_name old))
   (not= (:email new) (:email old))
   (not= (:amount new) (:amount old))))

(defn can-be-updated? [old-ai new]
  (let [status (:status old-ai)]
    (cond
     (= status :overdue) (maksut-error :invoice-invalidstate-overdue "Ei voi muuttaa, eräpäivä mennyt")
     (= status :paid) (maksut-error :invoice-invalidstate-paid "Ei voi muuttaa, eräpäivä mennyt")
     (not= (:origin old-ai) (:origin new)) (maksut-error :invoice-createerror-originclash "Sama lasku eri lähteestä on jo olemassa")))
  true)

(defn get-lasku [db order-id]
  (get-lasku-by-order-id db {:order-id order-id}))


(defn get-laskut-by-secret [db secret]
  (all-linked-laskut-by-secret db {:secret secret}))

(defn create-payment [db pt-params]
  (log/info "process succ2 " pt-params)
  (log/info "process succ2.1 " (:ORDER_NUMBER pt-params))
  (with-db-transaction [tx db]
      (when-let [lasku (get-lasku-locked tx {:order-id (:ORDER_NUMBER pt-params)})]
        (log/info "process succ3 " lasku)
        (let [lasku-id (:id lasku)
              payment-id (:PAYMENT_ID pt-params)
              old-payment (select-payment tx {:invoice-id lasku-id :payment-id payment-id})]
          (log/info "process succ4 " lasku-id " p-id " payment-id)
            (if (some? old-payment)
              (do
                (log/warn "Old payment with same payment-id found, duplicate notification " old-payment))
              (do
                (log/warn "New payment found, adding new for" )
                (insert-payment! tx {
                                   :invoice-id lasku-id
                                   :payment-id payment-id
                                   :amount (bigdec (:AMOUNT pt-params))
                                   :timestamp (:TIMESTAMP pt-params)}) ;epoch seconds
                )

          )))))

(defn create-or-update-lasku [db lasku]
  (or
    (with-db-transaction [tx db]
      (when-let [current (get-lasku-locked tx {:order-id (:order-id lasku)})]
        (log/warn (str "Invoice with order-id " (:order_id current) " already exists, with origin " (:origin current)))
        ;need to fetch this again to get status-field (current is only used for locking,
        ;current_ai has all the same fields and more
        (let [current_ai (get-lasku-by-order-id tx {:order-id (:order-id lasku)})]
          ;TODO generate secret if it doesn't exist already

          (when (and (has-changed? current_ai lasku) (can-be-updated? current_ai lasku))
               (log/info (str "Incoming input has changed fields, and they will be updated "
                            (select-keys lasku [:first-name :last-name :email :amount])))
               (update-lasku! tx (select-keys lasku [:first-name :last-name :email :amount :order-id])))))
      (get-lasku-by-order-id tx {:order-id (:order-id lasku)}))
    (insert-lasku-returning! db lasku))
  )

(defn update-lasku [db lasku]
  (with-db-transaction [tx db]
     (let []
       (update-lasku! tx lasku))))
