(ns maksut.events.maksut-events
  (:require [maksut.macros.event-macros :as events]
            [maksut.api-schemas :as schemas]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [maksut.events.alert-events :as alert-events]))

;; Polut (avain DB:ssä)
(def root-maksut-path [:maksut])
(def maksut-invoice (conj root-maksut-path :invoice))
(def maksut-payment-form (conj root-maksut-path :payment-form))
(def maksut-secret (conj root-maksut-path :secret))

;; Events
(def get-invoices-by-secret :maksut/get-invoices-by-secret)
(def get-payment-form :maksut/get-payment-form)
(def set-maksut-secret :maksut/set-maksut-secret)
;(def clear-invoice :maksut/clear-invoice)
;(def clear-payment-form :maksut/clear-payment-form)
;(def clear-db-after-payment :maksut/clear-db-after-payment)
(def handle-get-invoices-response :maksut/handle-get-invoices-response)
(def handle-get-payment-form :maksut/handle-get-payment-form)

;; Käsittelijät
(events/reg-event-db-validating
 handle-get-invoices-response
 (fn-traced [db [response]]
            (->>
               response
               (assoc-in db maksut-invoice))))

(events/reg-event-db-validating
 handle-get-payment-form
 (fn-traced [db [response]]
            (prn "handle-get-payment-form " response)
            (->>
             response
             (assoc-in db maksut-payment-form))))

;(events/reg-event-db-validating
; clear-invoice
; (fn-traced [{db :db}]
;            (prn "clear-invoice " )
;            {:db       (update-in db maksut-invoice (constantly nil))}
;            ))
;
;(events/reg-event-db-validating
; clear-payment-form
; (fn-traced [{db :db}]
;            (prn "clear-payment-form " )
;            {:db       (update-in db maksut-payment-form (constantly nil))}
;            ))
;
;(events/reg-event-db-validating
; clear-db-after-payment
; (fn-traced []
;            (prn "clear-db-after-payment " )
;            {:dispatch-n [[clear-invoice]
;                          [clear-payment-form]]}
;            ))

(events/reg-event-fx-validating
 get-invoices-by-secret
 (fn-traced [{db :db} [secret]]
            (let [http-request-id get-invoices-by-secret]
              (js/console.log "before get-invoices-by-secret " secret)
              {:db   (update db :requests (fnil conj #{}) http-request-id)
               :http {:method           :get
                      :http-request-id  http-request-id
                      :path             "/maksut/api/laskut-by-secret"
                      :search-params    [[:secret secret]]
                      :response-schema  schemas/Laskut
                      :response-handler [handle-get-invoices-response]
                      :error-handler    [alert-events/http-request-failed]}})))

(events/reg-event-fx-validating
 get-payment-form
 (fn-traced [{db :db} [order-id]]
            (let [http-request-id get-payment-form
                  secret (-> db :maksut :secret)]
              {:db   (update db :requests (fnil conj #{}) http-request-id)
               :http {:method           :get
                      :http-request-id  http-request-id
                      :path             (str "/maksut/api/lasku/" order-id "/maksa") ;TODO HC, do url-encoding
                      :search-params    [[:secret secret]]
                      ;:response-schema  schemas/Laskut
                      :response-handler [handle-get-payment-form]
                      :error-handler    [alert-events/http-request-failed]}})))

(events/reg-event-db-validating
 set-maksut-secret
 (fn-traced [db [secret]]
            (assoc-in db maksut-secret secret)))




