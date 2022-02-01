(ns maksut.authentication.auth-routes
  (:require [clj-ring-db-session.authentication.login :as crdsa-login]
            [clj-ring-db-session.session.session-store :as oph-session]
            [com.stuartsierra.component :as component]
            [maksut.audit-logger-protocol :as audit]
            [maksut.cas.cas-ticket-client-protocol :as cas-ticket-client-protocol]
            [maksut.config :as c]
            [maksut.kayttooikeus.kayttooikeus-protocol :as kayttooikeus-protocol]
            [maksut.oph-url-properties :as url]
            [maksut.schemas.class-pred :as p]
            [ring.util.http-response :refer [ok]]
            [ring.util.response :as resp]
            [schema.core :as s]
            [taoensso.timbre :as log])
  (:import javax.sql.DataSource
           (fi.vm.sade.utils.cas CasLogout)))

(defprotocol AuthRoutesSource
  (login [this ticket request])
  (cas-logout [this request])
  (logout [this session]))

(def kirjautuminen (audit/->operation "kirjautuminen"))

(defn- merged-session [request response _]
  (let [request-session (:session request)
        response-session (:session response)]
    (-> response-session
        (merge (select-keys request-session [:key :user-agent])))))

(defn- login-success [audit-logger request response virkailija _ ticket]
  (let [session (merged-session request response virkailija)]
    (s/validate (p/extends-class-pred audit/AuditLoggerProtocol) audit-logger)
    (s/validate s/Str ticket)
    (assoc response :session session)))

(defn- login-failed
  ([login-failed-url e]
   (log/error e "Error in login ticket handling")
   (resp/redirect login-failed-url))
  ([login-failed-url]
   (resp/redirect login-failed-url)))

(defn- cas-initiated-logout [session-store logout-request]
  (log/info "cas-initiated logout")
  (let [ticket (CasLogout/parseTicketFromLogoutRequest logout-request)]
    (log/info "logging out ticket" ticket)
    (if (.isEmpty ticket)
      (log/error "Could not parse ticket from CAS request" logout-request)
      (crdsa-login/cas-initiated-logout (.get ticket) session-store))
    (ok)))

(defrecord AuthRoutesMaker [config
                            db
                            cas-ticket-validator
                            kayttooikeus-service
                            audit-logger]
  component/Lifecycle
  (start [this]
    (s/validate (s/pred #(instance? DataSource %)) (:datasource db))
    (s/validate c/MaksutConfig config)
    (s/validate (p/extends-class-pred cas-ticket-client-protocol/CasTicketClientProtocol) cas-ticket-validator)
    (s/validate (p/extends-class-pred kayttooikeus-protocol/KayttooikeusService) kayttooikeus-service)
    (s/validate (p/extends-class-pred audit/AuditLoggerProtocol) audit-logger)
    (s/validate s/Str (get-in config [:urls :maksut-url]))
    (s/validate s/Str (url/resolve-url :cas.failure config))
    (assoc this
      :maksut-url (get-in config [:urls :maksut-url])
      :login-failure-url (url/resolve-url :cas.failure config)
      :session-store (oph-session/create-session-store (:datasource db))))

  (stop [this]
    (assoc this
      :maksut-url nil
      :login-failure-url nil
      :session-store nil))

  AuthRoutesSource

  (login [this ticket request]
    (try
      (if-let [[username _] (cas-ticket-client-protocol/validate-service-ticket cas-ticket-validator ticket)]
        (let [redirect-url (or (get-in request [:session :original-url])
                               (:maksut-url this))
              virkailija (kayttooikeus-protocol/virkailija-by-username kayttooikeus-service username)
              response (crdsa-login/login
                         {:username             username
                          :ticket               ticket
                          :success-redirect-url redirect-url
                          :datasource           (:datasource db)})]
          (login-success audit-logger request response virkailija username ticket))
        (login-failed (:login-failure-url this)))
      (catch Exception e
        (login-failed (:login-failure-url this) e))))

  (cas-logout [this request]
    (-> this
        :session-store
        (cas-initiated-logout request)))

  (logout [_ session]
    (crdsa-login/logout session (url/resolve-url :cas.logout config))))
