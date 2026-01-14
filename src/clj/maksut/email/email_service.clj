(ns maksut.email.email-service
  "You can send any email with this, it's not tied to any particular email-type"
  (:require [maksut.oph-url-properties :as url]
            [maksut.caller-id :as caller-id]
            [maksut.email.email-service-protocol :refer [EmailServiceProtocol]]
            [maksut.config :as c]
            [com.stuartsierra.component :as component]
            [schema.core :as s]
            [taoensso.timbre :as log])
  (:import (fi.oph.viestinvalitys ClientBuilder ViestinvalitysClient$EndpointBuilder)
           (fi.oph.viestinvalitys.vastaanotto.model LuoViestiSuccessResponse)
           (fi.oph.viestinvalitys ViestinvalitysClient ViestinvalitysClientException)
           (org.simplejavamail.api.mailer.config TransportStrategy)
           (org.simplejavamail.mailer MailerBuilder)
           (org.simplejavamail.email EmailBuilder)))

(defn- send-email [this viesti]
  (try
    (let [url (get this :email-service-url)
          viestinvalitys-client ^ViestinvalitysClient (get this :viestinvalitys-client)
          response ^LuoViestiSuccessResponse (.luoViesti viestinvalitys-client viesti)]
      (log/info "email url " url)
      (log/info "email response " response))
    (catch ViestinvalitysClientException e
      (log/error (str "Creating a viesti failed with status" (.getStatus e) " and validation errors: " (.getVirheet e)))
      (log/error "Sending email failed:" e)
      (throw e))
    (catch Exception e (log/error "Sending email failed:" e)
                       (throw e))))


(defrecord EmailService [config]
  component/Lifecycle
  (start [this]
    (s/validate c/MaksutConfig config)
    (let [url (url/resolve-url :viestinvalitys.endpoint config)
          cas-url (url/resolve-url :cas.url config)
          caller-id (-> config :oph-organisaatio-oid caller-id/make-caller-id)
          viestinvalitys-client (-> ^ViestinvalitysClient$EndpointBuilder (ClientBuilder/viestinvalitysClientBuilder)
                                   (.withEndpoint url)
                                   (.withUsername (-> config :cas :username))
                                   (.withPassword (-> config :cas :password))
                                   (.withCasEndpoint cas-url)
                                   (.withCallerId caller-id)
                                   (.build))]
      (assoc this :email-service-url url
                  :viestinvalitys-client viestinvalitys-client)))
  (stop [this]
    (assoc this
           :config nil
           :viestinvalitys-client nil
           ))

  EmailServiceProtocol
  (send-email [this viesti]
    (send-email this viesti)
  ))

(defn email-service [config]
  (map->EmailService config))

(defrecord MockEmailService [config mock-email-service-list]
  EmailServiceProtocol
  (send-email [_ viesti]
    (let [from ^String (-> viesti .getLahettaja .get .getSahkopostiOsoite .get)
          recipients (->> viesti .getVastaanottajat .get (map #(-> % .getSahkopostiOsoite .get)))
          subject ^String (-> viesti .getOtsikko .get)
          body ^String (-> viesti .getSisalto .get)
          mailer (-> (MailerBuilder/withSMTPServerHost "localhost")
                     (.withSMTPServerPort (int 1025))
                     (.withTransportStrategy TransportStrategy/SMTP)
                     (.buildMailer))
          mail (-> (EmailBuilder/startingBlank)
                   (.from from)
                   (.to (first recipients))
                   (.withSubject subject)
                   (.withHTMLText body)
                   (.buildEmail))]
      (.sendMail mailer mail)
      (reset! mock-email-service-list
              (conj @mock-email-service-list
                    {:from       from
                     :recipients recipients
                     :subject    subject
                     :body       body})))))
