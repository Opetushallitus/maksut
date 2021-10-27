(ns maksut.session-timeout
  (:require [cheshire.core :as json]
            [clojure.string :refer [starts-with?]]
            [maksut.config :as c]
            [maksut.oph-url-properties :refer [resolve-url]]
            [ring.middleware.session-timeout :as session-timeout]
            [ring.util.http-response :as response]
            [schema.core :as s]))

(defonce absolute-timeout (* 60 60))

(s/defn ^:private create-timeout-handler
  [config :- c/MaksutConfig]
  (fn [{:keys [uri]}]
    (let [auth-url (resolve-url :cas.login config)]
      (if (starts-with? uri "/maksut/api")
        (response/unauthorized (json/generate-string {:redirect auth-url}))
        (response/found auth-url)))))

(defn- options-with-timeout [config timeout]
  {:timeout         timeout
   :timeout-handler (create-timeout-handler config)})

(s/defn create-wrap-absolute-session-timeout
  [config :- c/MaksutConfig]
  (fn [handler]
    (->> (options-with-timeout config absolute-timeout)
         (session-timeout/wrap-absolute-session-timeout handler))))
