(ns maksut.cas.session-id
  (:require [maksut.oph-url-properties :refer [resolve-url]]
            [clj-http.client :as client]
            [maksut.caller-id :as cid]
            [jsoup.soup :refer [parse attr $]]
            [taoensso.timbre :as log]
            [clojure.string :refer [blank?]]))

(defn add-headers [options]
  (let [caller-id (cid/make-caller-id "1.2.246.562.10.00000000001")]
    (-> options
        (assoc-in [:headers "Caller-id"] caller-id)
        (assoc-in [:headers "CSRF"] caller-id)
        (assoc-in [:cookies "CSRF"] {:value caller-id :path "/"}))))

(defn request [opts]
  (-> opts
      add-headers
      client/request))

(defn handle-error
  [url method-name response]
  (let [status   (:status response)
        body     (:body response)]
    (case status
      200 response
      404 (do (log/warn  "Got " status " from " method-name ": " url " with body " body) nil)
      nil (do (log/error  "Got " status " from " method-name ": " url " with error: " (if (instance? Exception response) (.getMessage response) response)) nil)
      (do (log/error "Got " status " from " method-name ": " url " with response " response) nil))))

(defn- send-form
  [url form]
  (request {:form-params      form
            :method           :post
            :url              url
            :throw-exceptions false
            :content-type     "application/x-www-form-urlencoded"}))

(defn- parse-ticket-granting-ticket
  [response]
  (let [parsed-body (-> response :body parse)]
    (first (attr "action" ($ parsed-body "form")))))

(defn- get-ticket-granting-ticket
  [config]
  (let [username (get-in config [:cas :username])
        password (get-in config [:cas :password])
        response (send-form (resolve-url :cas.tickets config) {:username username :password password})
        tgt (parse-ticket-granting-ticket response)]
    (if (blank? tgt)
      (throw (RuntimeException. (format "Unable to read tgt on CAS response: %s" response)))
      tgt)))

(defn- get-service-ticket
  [service-url tgt]
  (let [response (send-form tgt {:service service-url})]
    (if-let [st (:body response)]
      st
      (throw (RuntimeException. (format "Unable to parse service ticket for service %s on responce: %s!" service-url response))))))

(defn- parse-jsession-id
  [response]
  (or (when-let [cookie (-> response :headers :set-cookie)]
        (nth (re-find #"JSESSIONID=(\w*);" cookie) 1 nil))
      (some-> response :cookies (get "JSESSIONID") :value)))

(defn- get-jsession-id
  [service-url st]
  (let [response (request {:headers {"CasSecurityTicket" st} :url service-url :method :get :throw-exceptions false})]
    (if-let [jsession-id (parse-jsession-id response)]
      jsession-id
      (throw (RuntimeException. (format "Unable to parse session ID from %s on response: %s" service-url response))))))

(defn- get-session-id
  [service-url st]
  (let [url (str service-url "?ticket=" st)
        response (request {:url url :method :get :throw-exceptions false :follow-redirects false})]
    (if-let [session-id (-> response :cookies (get "session") :value)]
      session-id
      (throw (RuntimeException. (format "Unable to parse session ID! Uri = %s and response %s" url response))))))

(defn get-id
  [config service jsession?]
  (if jsession?
    (->> (get-ticket-granting-ticket config)
         (get-service-ticket (str (:url service) "/j_spring_cas_security_check"))
         (get-jsession-id (:url service)))
    (->> (get-ticket-granting-ticket config)
         (get-service-ticket (:url service))
         (get-session-id (:url service)))))