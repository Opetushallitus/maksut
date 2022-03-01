(ns maksut.cas.cas-authenticating-client
  (:require [com.stuartsierra.component :as component]
            [maksut.caller-id :as caller-id]
            [maksut.cas.cas-authenticating-client-protocol :as cas-authenticating-protocol]
            [maksut.cas.cas-client :as cas-client]
            [maksut.config :as c]
            [maksut.http :as http]
            [maksut.oph-url-properties :as url]
            [schema.core :as s]
            [taoensso.timbre :as log])
  (:import [java.net URI]))

(def auth-fail-status #{302 401})
(def error-status #{500 400})

(s/defschema PostOrPutOpts
  {:url  s/Str
   :body s/Any})

(s/defn do-cas-authenticated-request
  [{:keys [cas-client
           method
           url
           body]} :- {:cas-client            s/Any
                      :url                   s/Str
                      :method                http/HttpMethod
                      (s/optional-key :body) s/Any}
   schemas :- http/HttpValidation
   config :- c/MaksutConfig]
  (let [request-params (cond-> {}
                               (some? body)
                               (assoc :body body))
        response (cas-client/cas-authenticated-request-as-json config
                                                               cas-client
                                                               method
                                                               url
                                                               request-params)]
    response))

(defn- create-uri [url-key config]
  (s/validate c/MaksutConfig config)
  (-> (url/resolve-url url-key config)
      (URI/create)))

(defrecord CasAuthenticatingClient [config service]
  component/Lifecycle
  (start [this]
    (s/validate c/MaksutConfig config)
    (s/validate s/Keyword service)
    (let [{:keys [service-url-property]} (-> config :cas :services service)
          cas-session (cas-client/init-session (url/resolve-url service-url-property
                                                                config)
                                               true)]
      (assoc this
        :cas-client cas-session)))

  (stop [this]
    (assoc this
      :cas-client nil))

  cas-authenticating-protocol/CasAuthenticatingClientProtocol

  (post [this
           {:keys [url body] :as opts}
           schemas]
      (s/validate PostOrPutOpts opts)
      (do-cas-authenticated-request {:cas-client          (:cas-client this)
                                     :method              :post
                                     :url                 url
                                     :body                body}
                                    schemas
                                    config))

  (http-put [this
        {:keys [url body] :as opts}
        schemas]
    (s/validate PostOrPutOpts opts)
    (do-cas-authenticated-request {:cas-client          (:cas-client this)
                                   :method              :put
                                   :url                 url
                                   :body                body}
                                  schemas
                                  config))

  (get [this url response-schema]
    (do-cas-authenticated-request {:cas-client          (:cas-client this)
                                   :method              :get
                                   :url                 url}
                                  {:request-schema  nil
                                   :response-schema response-schema}
                                  config))

  (delete [this url response-schema]
    (do-cas-authenticated-request {:cas-client          (:cas-client this)
                                   :method              :delete
                                   :url                 url}
                                  {:request-schema  nil
                                   :response-schema response-schema}
                                  config)))
