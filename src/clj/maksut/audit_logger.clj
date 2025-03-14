(ns maksut.audit-logger
  (:require [clj-timbre-auditlog.audit-log :as cta-audit-log]
            [com.stuartsierra.component :as component]
            [maksut.audit-logger-protocol :as audit-logger-protocol]
            [maksut.config :as c]
            [schema.core :as s])
  (:import [fi.vm.sade.auditlog ApplicationType Audit]))

(defn- create-audit-log ^Audit [base-path]
  (cta-audit-log/create-audit-logger "maksut" base-path ApplicationType/OPPIJA))

(defrecord AuditLogger [config]
  component/Lifecycle

  (start [this]
    (s/validate c/MaksutConfig config)
    (assoc this :audit-log (create-audit-log (-> config :log :base-path))))

  (stop [this]
    (assoc this :audit-log nil))

  audit-logger-protocol/AuditLoggerProtocol
  (log [this user operation target changes]
    (.log (:audit-log this) user operation target changes)))

(defn audit-logger [config]
  (map->AuditLogger config))
