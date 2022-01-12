(ns maksut.subs.maksut-subs
  (:require [re-frame.core :as re-frame]
            [maksut.i18n.utils :as i18n-utils]
            [maksut.events.maksut-events :as maksut-events]))

;; Subscriptions
(def maksut-invoice :maksut/invoice)
(def maksut-invoice-error :maksut/invoice-error)
(def maksut-invoice-fullname :maksut/invoice-fullname)
(def maksut-is-loading :maksut/is-loading)
(def maksut-payment-form :maksut/payment-form)
(def maksut-secret :maksut/secret)

(re-frame/reg-sub
 maksut-payment-form
 (fn [db _]
   (get-in db maksut-events/maksut-payment-form)))

(re-frame/reg-sub
 maksut-invoice
 (fn [db _]
   (get-in db maksut-events/maksut-invoice)))

(re-frame/reg-sub
 maksut-invoice-error
 (fn [db _]
   (get-in db maksut-events/maksut-invoice-error)))

(re-frame/reg-sub
 maksut-invoice-fullname
 (fn [db _]
   (let [laskut (get-in db maksut-events/maksut-invoice)
         lasku  (first laskut)
         name   (str (:first_name lasku) " " (:last_name lasku))]
     name)))

(re-frame/reg-sub
 maksut-secret
 (fn [db _]
   (get-in db maksut-events/maksut-secret)))

(re-frame/reg-sub
 maksut-is-loading
 (fn [db _]
   (let [requests (:requests db)]
     (contains? requests maksut-events/get-invoices-by-secret))))



