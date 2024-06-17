(ns maksut.system
  (:require [com.stuartsierra.component :as component]
            [maksut.audit-logger :as audit-logger]
            [maksut.authentication.auth-routes :as auth-routes]
            [maksut.cas.cas-authenticating-client :as authenticating-client]
            [maksut.cas.cas-ticket-client :as cas-ticket-validator]
            [maksut.config :as c]
            [maksut.db :as db]
            [maksut.files.filesystem-store :as filesystem-store]
            [maksut.files.s3-client :as s3-client]
            [maksut.files.s3-store :as s3-store]
            [maksut.maksut.maksut-service :as maksut-service]
            [maksut.payment.payment-service :as payment-service]
            [maksut.lokalisaatio.lokalisaatio-service :as lokalisaatio-service]
            [maksut.email.email-service :as email-service]
            [maksut.health-check :as health-check]
            [maksut.kayttooikeus.kayttooikeus-service :as kayttooikeus-service]
            [maksut.migrations :as migrations]
            [maksut.server :as http]))

(defn maksut-system [config]
  (let [base-system       [:audit-logger (audit-logger/map->AuditLogger {:config config})

                           :db (db/map->DbPool {:config config})

                           :migrations (component/using
                                         (migrations/map->Migrations {})
                                         [:db])

                           :maksut-service (component/using
                                            (maksut-service/map->MaksutService {:config config})
                                            [:audit-logger
                                             :db])

                           :payment-service (component/using
                                             (payment-service/map->PaymentService {:config config})
                                             [:audit-logger
                                              :email-service
                                              :db
                                              :storage-engine])

                           :lokalisaatio-service (component/using
                                                   (lokalisaatio-service/map->LokalisaatioService {:config config})
                                                   [])

                           :health-checker (component/using
                                             (health-check/map->DbHealthChecker {})
                                             [:db])

                           :auth-routes-source (component/using
                                                (auth-routes/map->AuthRoutesMaker {:config config})
                                                [:db
                                                  :cas-ticket-validator
                                                  :kayttooikeus-service
                                                  :audit-logger])

                           :http-server (component/using
                                          (http/map->HttpServer {:config config})
                                                  [:db
                                                   :migrations
                                                   :health-checker
                                                   :maksut-service
                                                   :payment-service
                                                   :email-service
                                                   :lokalisaatio-service
                                                   :auth-routes-source])]

        production-system [:kayttooikeus-authenticating-client (authenticating-client/map->CasAuthenticatingClient {:service :kayttooikeus
                                                                                                                     :config  config})

                           :kayttooikeus-service (component/using (kayttooikeus-service/map->HttpKayttooikeusService {:config config})
                                                    [:kayttooikeus-authenticating-client])

                           :email-authenticating-client (authenticating-client/map->CasAuthenticatingClient {:service :email
                                                                                                             :config  config})

                           :email-service (component/using (email-service/map->EmailService {:config config})
                                           [:audit-logger
                                            :db
                                            :email-authenticating-client])

                           :cas-ticket-validator (cas-ticket-validator/map->CasTicketClient {:config config})]
        mock-system       [:cas-ticket-validator (cas-ticket-validator/map->FakeCasTicketClient {})

                           :kayttooikeus-service (kayttooikeus-service/->FakeKayttooikeusService)

                           :mock-email-service-list (atom '())

                           :email-service (component/using (email-service/map->MockEmailService {:config config})
                                                           [:mock-email-service-list])]

        files             (case (get-in config [:file-store :engine])
                            :filesystem [:config config
                                         :storage-engine (component/using
                                                           (filesystem-store/new-store)
                                                           [:config])]
                            :s3 [:config config
                                 :s3-client (component/using
                                              (s3-client/new-client)
                                              [:config])
                                 :storage-engine (component/using
                                                   (s3-store/new-store)
                                                   [:s3-client :config])])
        system            (into
                            (into base-system
                                  (if (or (c/development-environment? config) (c/integration-environment? config))
                                    mock-system
                                    production-system)) files)]
    (apply component/system-map system)))
