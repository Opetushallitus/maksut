(ns maksut.cas.cas-authenticating-client
  (:require [com.stuartsierra.component :as component]
            [maksut.cas.cas-authenticating-client-protocol :as cas-authenticating-protocol]
            [maksut.cas.cas-client :as cas-client]
            [maksut.config :as c]
            [maksut.http :as http]
            [maksut.oph-url-properties :as url]
            [schema.core :as s]))

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
           _]
      (s/validate PostOrPutOpts opts)
      (do-cas-authenticated-request {:cas-client          (:cas-client this)
                                     :method              :post
                                     :url                 url
                                     :content-type        "application/json;charset=utf-8"
                                     :body                body}
                                    config))

  (http-put [this
        {:keys [url body] :as opts}
        _]
    (s/validate PostOrPutOpts opts)
    (do-cas-authenticated-request {:cas-client          (:cas-client this)
                                   :method              :put
                                   :url                 url
                                   :content-type        "application/json;charset=utf-8"
                                   :body                body}
                                  config))

  (get [this url _]
    (do-cas-authenticated-request {:cas-client          (:cas-client this)
                                   :method              :get
                                   :url                 url}
                                  config))

  (delete [this url _]
    (do-cas-authenticated-request {:cas-client          (:cas-client this)
                                   :method              :delete
                                   :url                 url}
                                  config)))
