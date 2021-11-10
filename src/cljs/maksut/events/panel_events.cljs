(ns maksut.events.panel-events
  (:require
    [maksut.macros.event-macros :as events]
    [day8.re-frame.tracing :refer-macros [fn-traced]]
    [maksut.events.alert-events :as alert-events]
    [maksut.events.maksut-events :as maksut-events]
    [maksut.events.translation-events :as transl-events]))

(defn- make-tutu-asetukset-dispatches [{:keys [query]}]
  (let [{:keys [secret payment]} query]
    [(when (some? secret) [maksut-events/set-maksut-secret secret])
     (when (= payment "cancel") [alert-events/payment-canceled])
     [maksut-events/get-invoices-by-secret secret]]))

(def ^:private translation-dispatches
  [[transl-events/get-remote-translations :fi]
   [transl-events/get-remote-translations :sv]
   [transl-events/get-remote-translations :en]])

(defn- make-dispatches [{:keys [panel parameters]}]
  (when-let [make-fn (case panel
                       :panel/tutu-maksut make-tutu-asetukset-dispatches
                       :default nil)]
    (concat
      (make-fn parameters)
      ;translation-dispatches
      []
    )
    ))

(events/reg-event-fx-validating
  :panel/set-active-panel
  (fn-traced [{db :db} [active-panel]]
             (prn "SET-ACTIVE-PANEL " active-panel)
             (let [dispatches (make-dispatches active-panel)]
               (cond-> {:db (assoc db :active-panel active-panel)}
                       (seq dispatches)
                       (assoc :dispatch-n dispatches)))))
