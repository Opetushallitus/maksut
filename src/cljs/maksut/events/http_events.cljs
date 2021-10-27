(ns maksut.events.http-events
  (:require [day8.re-frame.tracing :refer-macros [fn-traced]]
            [maksut.macros.event-macros :as events]))

(events/reg-event-db-validating
  :http/remove-http-request-id
  (fn-traced [db [http-request-id]]
    (update db :requests (fnil disj #{}) http-request-id)))
