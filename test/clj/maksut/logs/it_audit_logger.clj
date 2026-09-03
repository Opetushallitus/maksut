(ns maksut.logs.it-audit-logger
  (:require [clojure.test :refer :all]
            [com.stuartsierra.component :as component]
            [maksut.logs.audit-logger-protocol :refer [AuditLoggerProtocol]]
            [maksut.logs.timbre-auditlog :as timbre-audit-log]
            [maksut.config :as c]
            [schema.core :as s])
  (:import [fi.vm.sade.auditlog ApplicationType Audit]))

(defn- create-audit-log ^Audit [base-path]
  (timbre-audit-log/create-audit-logger "maksut" base-path ApplicationType/OPPIJA))

; Integraatio-testeihin audit-logger, joka kirjoittaa logia myös erilliseen
; listaan, josta logit voi helposti lukea.
(defrecord ItAuditLogger [config mock-audit-log-list]
  component/Lifecycle
  (start [this]
    (s/validate c/MaksutConfig config)
    (assoc this :audit-log (create-audit-log (-> config :log :base-path))))

  (stop [this]
    (assoc this :audit-log nil))

  AuditLoggerProtocol
  (log [this user operation target changes]
    (.log (:audit-log this) user operation target changes)
    (reset! mock-audit-log-list
            (conj @mock-audit-log-list
                  {:user user
                   :operation operation
                   :target target
                   :changes changes}))))
