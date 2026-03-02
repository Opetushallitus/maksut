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

(defn send-email [this viesti]
  (try
    (let [viestinvalitys-client ^ViestinvalitysClient (get this :viestinvalitys-client)
          response ^LuoViestiSuccessResponse (.luoViesti viestinvalitys-client viesti)]
      (log/info "Email successfully sent with tunniste" (-> response .getViestiTunniste .toString)))
    (catch ViestinvalitysClientException e
      (log/error (str "Creating a viesti failed with status" (.getStatus e) " and validation errors: " (.getVirheet e)))
      (log/error "Sending email failed:" e)
      (throw e))
    (catch Exception e (log/error "Sending email failed:" e)
                       (throw e))))

(defn create-viestinvalitys-client [config]
  (s/validate c/MaksutConfig config)
  (let [url (url/resolve-url :viestinvalitys.endpoint config)
        cas-url (url/resolve-url :cas.url config)
        caller-id (-> config :oph-organisaatio-oid caller-id/make-caller-id)]
    (log/info "Using viestinvalitys-client url" url)
    (-> ^ViestinvalitysClient$EndpointBuilder (ClientBuilder/viestinvalitysClientBuilder)
        (.withEndpoint url)
        (.withUsername (-> config :cas :username))
        (.withPassword (-> config :cas :password))
        (.withCasEndpoint cas-url)
        (.withCallerId caller-id)
        (.build))))

(defrecord EmailService [config]
  component/Lifecycle
  (start [this]
    (assoc this :viestinvalitys-client (create-viestinvalitys-client config)))
  (stop [this]
    (assoc this :viestinvalitys-client nil))

  EmailServiceProtocol
  (send-email [this viesti]
    (send-email this viesti)))

; Palvelun ajamiseen paikallisesa ympäristössä.
; Lähettää sähköpostit SMTP:llä MailCatcherille, josta kehittäjä voi ne lukea.
(defrecord DevSmtpEmailService [config]
  EmailServiceProtocol
  (send-email [_ viesti]
    (let [^String from (-> viesti .getLahettaja .get .getSahkopostiOsoite .get)
          ^String recipient (-> viesti .getVastaanottajat .get first .getSahkopostiOsoite .get)
          ^String subject (-> viesti .getOtsikko .get)
          ^String body (-> viesti .getSisalto .get)
          mailer (-> (MailerBuilder/withSMTPServerHost "localhost")
                     (.withSMTPServerPort (int 1025))
                     (.withTransportStrategy TransportStrategy/SMTP)
                     (.buildMailer))
          mail (-> (EmailBuilder/startingBlank)
                   (.from from)
                   (.to recipient)
                   (.withSubject subject)
                   (.withHTMLText body)
                   (.buildEmail))]
      (.sendMail mailer mail))))
