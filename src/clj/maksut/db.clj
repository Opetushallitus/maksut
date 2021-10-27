(ns maksut.db
  (:require [cheshire.core :as json]
            [clj-time.coerce :as clj-time-coerce]
            [clojure.java.jdbc :as jdbc]
            [clojure.set :as cs]
            [com.stuartsierra.component :as component]
            [maksut.config :as c]
            [hikari-cp.core :as hikari]
            [schema.core :as s])
  (:import [java.sql PreparedStatement]
           [java.time LocalDate]
           [org.postgresql.util PGobject]))

(defn sql-date->LocalDate [v]
  (let [java-time-localdate (.toLocalDate v)]
    (. LocalDate (of (.getYear java-time-localdate)
                     (.getValue (.getMonth java-time-localdate))
                     (.getDayOfMonth java-time-localdate)))))

(defn sql-date-Timestamp->LocalDate [v]
  (let [java-time-localdate (.toLocalDate (.toLocalDateTime v))]
    (. LocalDate (of (.getYear java-time-localdate)
                     (.getValue (.getMonth java-time-localdate))
                     (.getDayOfMonth java-time-localdate)))))

(extend-protocol jdbc/ISQLValue
  clojure.lang.IPersistentCollection
  (sql-value [value]
    (doto (PGobject.)
      (.setType "jsonb")
      (.setValue (json/generate-string value)))))

(extend-protocol jdbc/IResultSetReadColumn
  PGobject
  (result-set-read-column [pgobj _ _]
    (let [type  (.getType pgobj)
          value (.getValue pgobj)]
      (case type
        "json" (json/parse-string value true)
        "jsonb" (json/parse-string value true)
        :else value))))

(extend-protocol jdbc/IResultSetReadColumn
  java.sql.Date
  (result-set-read-column [v _ _] (sql-date->LocalDate v))

  ;Note. Converts Timestamps to just the date, if we need times too this needs to be modified
  java.sql.Timestamp
  (result-set-read-column [v _ _] (sql-date-Timestamp->LocalDate v))

  org.postgresql.jdbc.PgArray
  (result-set-read-column [v _ _]
    (vec (.getArray v))))

;TODO replace this with java.util.LocalDate handling if need to pass dates also
;(extend-type org.joda.time.DateTime
;  jdbc/ISQLParameter
;  (set-parameter [v ^PreparedStatement stmt idx]
;    (.setTimestamp stmt idx (clj-time-coerce/to-sql-time v))))

(defrecord DbPool [config]
  component/Lifecycle

  (start [this]
    (s/validate c/MaksutConfig config)
    (let [datasource-options (merge
                               {:auto-commit        true
                                :read-only          false
                                :connection-timeout 30000
                                :validation-timeout 5000
                                :idle-timeout       600000
                                :max-lifetime       1800000
                                :minimum-idle       10
                                :maximum-pool-size  10
                                :pool-name          "db-pool"
                                :adapter            "postgresql"
                                :register-mbeans    false}
                               (-> config
                                   :db
                                   (select-keys [:username
                                                 :password
                                                 :database-name
                                                 :host
                                                 :port])
                                   (cs/rename-keys {:host :server-name
                                                    :port :port-number})))
          datasource         (hikari/make-datasource datasource-options)]
      (assoc this :datasource datasource)))

  (stop [this]
    (when-let [datasource (:datasource this)]
      (hikari/close-datasource datasource))
    (assoc this :datasource nil)))
