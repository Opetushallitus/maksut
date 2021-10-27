(ns maksut.events.core-events
  (:require
    [maksut.db :as db]
    [maksut.macros.event-macros :as events]
    [day8.re-frame.tracing :refer-macros [fn-traced]]))

(events/reg-event-db-validating
  :core/initialize-db
  (fn-traced [_ _]
    db/default-db))
