(ns maksut.audit-logger-protocol
  (:require [maksut.authentication.schema :as auth-schema]
            [schema.core :as s])
  (:import [fi.vm.sade.auditlog
            User
            Operation
            Target$Builder
            Changes$Builder]
           java.net.InetAddress
           org.ietf.jgss.Oid))

(defprotocol AuditLoggerProtocol
  (log [this user operation target changes]))

(s/defn ->user [session :- auth-schema/Session]
  (User.
    (when-let [oid (-> session :identity :oid)]
      (Oid. oid))
    (if-let [ip (:client-ip session)]
      (InetAddress/getByName ip)
      (InetAddress/getLocalHost))
    (or (:key session) "no session")
    (or (:user-agent session) "no user agent")))

(s/defn ->operation [name-str :- s/Str]
  (proxy [Operation] [] (name [] name-str)))

(s/defn ->target [m :- {s/Keyword s/Str}]
  (.build
   (reduce (fn [builder [k s]]
             (.setField builder (name k) s))
           (new Target$Builder)
           m)))

(declare ChangedObject)

(s/defschema Value
  (s/cond-pre (s/recursive #'ChangedObject)
              s/Bool
              s/Num
              s/Keyword
              s/Int
              s/Str))

(s/defschema ChangedObject
  (s/cond-pre {(s/cond-pre s/Keyword s/Str) Value}
              [Value]))

(defn- object->fields-and-values [o]
  (mapcat (fn [[k v]]
            (if (or (map? v) (sequential? v))
              (map (fn [[kk vv]]
                     [(str (if (keyword? k) (name k) k) "." kk) vv])
                   (object->fields-and-values v))
              [[(if (keyword? k) (name k) k) (str v)]]))
          (if (sequential? o) (map-indexed vector o) o)))

(s/defn ->changes [old-object :- ChangedObject
                   new-object :- ChangedObject]
  (let [old-fields (into {} (object->fields-and-values old-object))
        new-fields (into {} (object->fields-and-values new-object))]
    (.build
     (reduce (fn [builder field]
               (cond (and (contains? old-fields field)
                          (not (contains? new-fields field)))
                     (.removed builder field (get old-fields field))
                     (and (not (contains? old-fields field))
                          (contains? new-fields field))
                     (.added builder field (get new-fields field))
                     (not= (get old-fields field) (get new-fields field))
                       (.updated builder field (get old-fields field) (get new-fields field))
                     :else builder))
             (new Changes$Builder)
             (distinct (concat (keys old-fields) (keys new-fields)))))))
