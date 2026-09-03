(ns maksut.test-system
  (:require [clojure.test :refer :all]
            [com.stuartsierra.component :as component]
            [maksut.cas.cas-ticket-client :as cas-ticket-validator]
            [maksut.email.it-email-service :as it-email-service]
            [maksut.logs.it-audit-logger :as it-audit-logger]
            [maksut.system :as system]))

(defn maksut-system [config]
  (let [it-system         [:cas-ticket-validator (cas-ticket-validator/map->FakeCasTicketClient {})
                           :mock-email-service-list (atom '())
                           :email-service (component/using (it-email-service/map->ItEmailService {:config config})
                                                           [:mock-email-service-list])
                           :mock-audit-log-list (atom '())
                           :audit-logger (component/using (it-audit-logger/map->ItAuditLogger {:config config})
                                                          [:mock-audit-log-list])]
        system (-> (system/base-system config)
                   (into it-system)
                   (into (system/files config) ))]
    (apply component/system-map system)))
