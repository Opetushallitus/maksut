(ns maksut.maksut.db.maksut-queries
  (:require [maksut.error :refer [maksut-error]]
            [maksut.util.random :as random]
            [hugsql.core :as hugsql]
            [clojure.java.jdbc :refer [with-db-transaction]]
            [taoensso.timbre :as log]))


(hugsql/def-db-fns "maksut/maksut/db/maksut_queries.sql")

(def unique-violation "23505")

;; HugSQL templatessa määritetyt kyselyt
(declare insert-lasku!)
(declare update-lasku!)
(declare get-lasku-by-order-id)
(declare get-linked-lasku-statuses-by-reference)
(declare all-linked-laskut-by-secret)
(declare all-linked-laskut-by-reference)
(declare get-lasku-locked)
(declare select-payment)
(declare insert-payment!)
(declare insert-secret-for-invoice!)

(defn- insert-new-secret [db invoice-id order-id]
  ;prefix secrets with order-id to force them unique even if random would generate two identical
  (let [prefix     (random/base64-string order-id)
        secret     (random/url-part 34)
        full       (str prefix secret)]
    (insert-secret-for-invoice! db
                                {:invoice-id invoice-id
                                 :secret     full})))

(defn- insert-lasku-create-secret [db lasku]
  (let [{:keys [id]} (insert-lasku! db lasku)
        order-id (:order-id lasku)]
    (log/info "Created invoice id=" id ", generating secret...")
    (insert-new-secret db id order-id)
    (get-lasku-by-order-id db {:order-id order-id})))

(defn has-changed? [old new]
  (or
   (not= (:first-name new) (:first_name old))
   (not= (:last-name new) (:last_name old))
   (not= (:email new) (:email old))
   (not= (:amount new) (:amount old))
   (not= (:metadata new) (:metadata old))))

(defn can-be-updated? [old-ai new]
  (let [status      (:status old-ai)
        same-origin (= (:origin old-ai) (:origin new))]
    (cond
     (= status "overdue") (maksut-error :invoice-invalidstate-overdue (str "Ei voi muuttaa, eräpäivä mennyt: " new))
     (= status "paid")    (maksut-error :invoice-invalidstate-paid (str "Ei voi muuttaa, lasku on jo maksettu: " new))
     (not same-origin)   (maksut-error :invoice-createerror-originclash (str "Sama lasku eri lähteestä on jo olemassa: " new)))
    true))

(defn get-lasku [db order-id]
  (get-lasku-by-order-id db {:order-id order-id}))


(defn get-laskut-by-secret [db secret]
  (all-linked-laskut-by-secret db {:secret secret}))

(defn get-laskut-by-reference [db reference]
  (all-linked-laskut-by-reference db {:reference reference}))

(defn check-laskut-statuses-by-reference [db refs]
  (get-linked-lasku-statuses-by-reference db {:refs refs}))

(defn create-payment [db order-number payment-id amount timestamp]
  (with-db-transaction
   [tx db]
   (if-let [lasku (get-lasku-locked tx {:order-id order-number})]
     (let [lasku-id    (:id lasku)
           old-payment (select-payment tx {:invoice-id lasku-id :payment-id payment-id})
           response    {:order-id  (:order_id lasku)
                        :email     (:email lasku)
                        :origin    (:origin lasku)
                        :reference (:reference lasku)
                        :first-name (:first_name lasku)
                        :last-name (:last_name lasku)
                        :form-name (get-in lasku [:metadata :form-name])
                        :vat (:vat lasku)
                        :amount-without-vat (:amount lasku)}]
       (if (some? old-payment)
         (do
           (log/warn "Old payment with same payment-id found, duplicate notification " old-payment)
           (assoc response :action :not-modified))
         (do
           (log/warn "New payment found, adding new for")
           (insert-payment! tx
                            {:invoice-id lasku-id
                             :payment-id payment-id
                             :amount     (bigdec amount)
                             :timestamp  timestamp}) ;epoch seconds
           (assoc response :action :created))))
     (do
       (log/error "Payment cannot be processed as invoice not found for order_number " order-number)
       ;Not throwing exception or returning non-success http-status as otherwise Paytrail will not
       ;accept this notify as received and will do the same request again multiple times
       {:action    :error
        :code      :invoice-notfound}))))

(defn create-or-update-lasku [db lasku]
  (with-db-transaction
     [tx db]
     ;UPDATE if exists and has been changed
     (log/warn (str "Trying to get lock for " (:order-id lasku)))
     (when-let [lock (get-lasku-locked tx {:order-id (:order-id lasku)})]
       (log/warn (str "Invoice with order-id " (:order_id lock) " already exists, with origin " (:origin lock)))
       ;need to fetch this again to get status-field (current is only used for locking),
       ;current_ai has all the same fields and more
       (let [current_ai (get-lasku-by-order-id tx {:order-id (:order-id lasku)})]
         (when (and (has-changed? current_ai lasku) (can-be-updated? current_ai lasku))
               (log/info (str "Incoming input has changed fields, and they will be updated"))
               (update-lasku! tx (select-keys lasku [:first-name :last-name :email :amount :order-id :metadata])))))

     (or
      ;RETURN previous (potentially updated version), if any
      (get-lasku-by-order-id tx {:order-id (:order-id lasku)})
      ;or CREATE new
      (insert-lasku-create-secret db lasku))))



