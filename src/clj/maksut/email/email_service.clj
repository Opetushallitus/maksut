(ns maksut.email.email-service
  "You can send any email with this, it's not tied to any particular email-type"
  (:require [maksut.oph-url-properties :as url]
            [maksut.email.email-service-protocol :refer [EmailServiceProtocol]]
            [maksut.cas.cas-authenticating-client-protocol :as authenticating-client]
            [maksut.config :as c]
            [com.stuartsierra.component :as component]
            [schema.core :as s]
            [taoensso.timbre :as log]))

(defn- send-email [this from recipients subject body]
  (let [url                (get this :email-service-url)
        cas-client         (get this :cas-client)
        wrapped-recipients (mapv (fn [rcp] {:email rcp}) recipients)
        body-content       {:email     {:from    from
                                        :subject subject
                                        :isHtml  true
                                        :body    body}
                            :recipient wrapped-recipients}
        schemas            {:request-schema  nil
                            :response-schema nil}
        response            (authenticating-client/post cas-client {:url url :body body-content} schemas)
        ]
    (log/info "email url " url)

    (log/info "email response " response)
    (when (not= 200 (:status response))
      (throw (Exception. (str "Could not send email to " (apply str recipients)))))))


(defrecord EmailService [config email-authenticating-client]
  component/Lifecycle
  (start [this]
    (s/validate c/MaksutConfig config)
    (let [url (url/resolve-url :ryhmasahkoposti-service.email config)]
      (assoc this :email-service-url url
                  :cas-client email-authenticating-client)))
  (stop [this]
    (assoc this
           :config nil
           :cas-client nil
           ))

  EmailServiceProtocol
  (send-email [this from recipients subject body]
    (send-email this from recipients subject body)
  ))

(defn email-service [config]
  (map->EmailService config))

(defrecord MockEmailService [config mock-email-service-list]
  EmailServiceProtocol
  (send-email [_ from recipients subject body]
    (reset! mock-email-service-list
            (conj @mock-email-service-list
                  {:from       from
                   :recipients recipients
                   :subject    subject
                   :body       body}))))
