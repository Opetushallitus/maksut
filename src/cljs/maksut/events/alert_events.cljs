(ns maksut.events.alert-events
  (:require [maksut.macros.event-macros :as events]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [maksut.i18n.utils :as i18n-utils]))

;Polut
(def root-path [:alert])
(def alert-id-path (conj root-path :id))
(def alert-message-path (conj root-path :message))

;Tapahtumat
(def alert-closed :alert/alert-window-closed)
(def new-alert :alert/new-alert)
(def http-request-failed :alert/http-request-failed)
(def payment-canceled :alert/payment-canceled)

(defn- message-id []
  (.getTime (js/Date.)))

(defn- clear-alert [db]
  (-> db
      (assoc-in alert-message-path "")
      (assoc-in alert-id-path nil)))

(events/reg-event-fx-validating
 payment-canceled
 (fn-traced [{db :db}]
            (let [message (i18n-utils/get-translation (:lang db) (:translations db) :yleiset/maksu-keskeytetty)
                  id (message-id)]
              {:dispatch [new-alert message id]
               :dispatch-later [{:ms 10000 :dispatch [alert-closed id]}]})))

(events/reg-event-fx-validating
  http-request-failed
  (fn-traced [{db :db} [error]]
             (let [error-code (:code error)
                   server-error (and error-code (i18n-utils/get-translation (:lang db) (:translations db) (keyword "rest" error-code)))
                   message (or server-error
                               (i18n-utils/get-translation (:lang db) (:translations db) :yleiset/http-virhe))
                   id (message-id)]
               {:dispatch [new-alert message id]
                :dispatch-later [{:ms 10000 :dispatch [alert-closed id]}]})))

(events/reg-event-db-validating
  new-alert
  (fn-traced [db [message id]]
             (let [id (or id (message-id))]
               (-> db
                   (assoc-in alert-message-path message)
                   (assoc-in alert-id-path id)))))

(events/reg-event-db-validating
  alert-closed
  (fn-traced [db [id]]
             (let [current-id (get-in db alert-id-path)]
               (cond
                 (nil? id) (clear-alert db)
                 (= id current-id) (clear-alert db)
                 :else db))))
