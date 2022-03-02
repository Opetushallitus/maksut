(ns maksut.cas.cas-client
  (:refer-clojure :exclude [empty?])
  (:require [clojure.string :refer [split upper-case]]
            [maksut.cas.session-id :as cas-session-id]
            [taoensso.timbre :as log]))

(defrecord CasSession [service session-id jsession?])

(defn init-session
  [service-url jsession?]
  (let [path (.getPath (java.net.URI. service-url))
        service (first (split service-url #"/j_spring_cas_security_check"))]
    (log/info "Init cas session to service " path " @ url " service)
    (map->CasSession {:service {:url service :path path} :session-id (atom nil) :jsession? jsession?})))

(defn- empty?
  [cas-session]
  (let [session-id (:session-id cas-session)]
    (nil? @session-id)))

(defn- reset
  [config cas-session]
  (let [session-id (:session-id cas-session)]
    (reset! session-id (cas-session-id/get-id config (:service cas-session) (:jsession? cas-session)))))

(defn- assoc-cas-session-params
  [cas-session opts]
  (let [session-id (:session-id cas-session)]
    (-> opts
        (assoc :follow-redirects false :throw-exceptions false)
        (assoc-in [:cookies (if (:jsession? cas-session) "JSESSIONID" "session")] {:value @session-id :path "/"}))))

(defn cas-authenticated-request
  ([config cas-session opts]
   (when (empty? cas-session)
     (reset config cas-session))
   (let [http (fn [] (cas-session-id/request (assoc-cas-session-params cas-session opts)))
         res (http)]
     (if (<= 300 (:status res))
       (do (reset config cas-session)
           (http))
       res)))
  ([config cas-client method url opts]
   (cas-authenticated-request config cas-client (assoc opts :url url :method method))))

(defn cas-authenticated-request-as-json
  ([config cas-client method url opts]
   (let [method-name (upper-case (str method))]
     (log/debug method-name " => " url)
     (try
       (let [response (cas-authenticated-request config cas-client method url (assoc opts :follow-redirects false
                                                                                          :throw-exceptions false))]
         (cas-session-id/handle-error url method-name response))
       (catch Exception e (cas-session-id/handle-error url method-name e) (throw e)))))
  ([config cas-client method url]
   (cas-authenticated-request-as-json config cas-client method url {})))