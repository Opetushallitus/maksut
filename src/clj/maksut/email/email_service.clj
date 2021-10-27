(ns maksut.email.email-service
  "You can send any email with this, it's not tied to any particular email-type"
  (:require [maksut.oph-url-properties :as url]
            [maksut.util.http-util :as http-util]
            [maksut.email.email-service-protocol :refer [EmailServiceProtocol]]
            [maksut.config :as c]
            [com.stuartsierra.component :as component]
            [cheshire.core :as json]
            [schema.core :as s]
            [taoensso.timbre :as log]))

(defn- send-email [this from recipients subject body]
  (let [url                (get this :email-service-url)
        wrapped-recipients (mapv (fn [rcp] {:email rcp}) recipients)
        response           (http-util/do-post url {:headers      {"content-type" "application/json"}
                                                   :query-params {:sanitize "false"}
                                                   :body         (json/generate-string {:email     {:from    from
                                                                                                    :subject subject
                                                                                                    :isHtml  true
                                                                                                    :body    body}
                                                                                        :recipient wrapped-recipients})})]
    (log/info "email url " url)

    (log/info "email response " response)
    (when (not= 200 (:status response))
      (throw (Exception. (str "Could not send email to " (apply str recipients)))))))


(defrecord EmailService [config]
  component/Lifecycle
  (start [this]
    (s/validate c/MaksutConfig config)
    (let [url (url/resolve-url :ryhmasahkoposti-service.email config)]
      (assoc this :email-service-url url)))
  (stop [this]
    (assoc this
           :config nil
           ))

  EmailServiceProtocol
  (send-email [this from recipients subject body]
    (send-email this from recipients subject body)
  ))

(defn email-service [config]
  (map->EmailService config))
