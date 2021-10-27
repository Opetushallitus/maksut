(ns maksut.http
  (:require [cheshire.core :as json]
            [clj-http.client :as http]
            [maksut.caller-id :as caller-id]
            [maksut.config :as c]
            [maksut.schemas.schema-util :as schema-util]
            [schema.core :as s]
            [schema-tools.core :as st]))

(s/defschema HttpMethod
  (s/enum :post
          :put
          :get
          :delete))

(s/defschema HttpValidation
  {:request-schema  s/Any
   :response-schema s/Any})


(s/defn parse-and-validate
  [response :- (st/open-schema {:body s/Str})
   response-schema]
  (-> response
      :body
      (json/parse-string true)
      (st/select-schema response-schema schema-util/extended-json-coercion-matcher)))

(s/defn do-request
  [{:keys [body] :as opts} :- (st/open-schema
                                {:url    s/Str
                                 :method HttpMethod})
   {:keys [request-schema]} :- HttpValidation
   config :- c/MaksutConfig]
  (when request-schema
    (s/validate request-schema body))
  (let [csrf-value "maksut"
        caller-id  (-> config :oph-organisaatio-oid caller-id/make-caller-id)
        opts       (-> opts
                       (assoc :redirect-strategy :none)
                       (assoc :throw-exceptions false)
                       (update :body json/generate-string)
                       (merge {:accept       :json
                               :content-type "application/json"})
                       (update :connection-timeout (fnil identity 240000))
                       (update :socket-timeout (fnil identity 240000))
                       (update :headers merge
                               {"Caller-Id" caller-id
                                "CSRF"      csrf-value})
                       (update :cookies merge {"CSRF" {:value csrf-value :path "/"}}))]
    (http/request opts)))
