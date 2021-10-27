(ns maksut.server
  (:require [com.stuartsierra.component :as component]
            [maksut.authentication.auth-routes :as auth-routes]
            [maksut.config :as c]
            [maksut.handler :as h]
            [maksut.health-check :as health]
            [maksut.schemas.class-pred :as p]
            [ring.adapter.jetty :as jetty]
            [schema.core :as s])
  (:import org.eclipse.jetty.server.handler.ErrorHandler))

(defonce jetty-error-handler
  (proxy [ErrorHandler] []
    (handleErrorPage [_ writer _ _]
      (.write writer "Internal server error\n"))))

(defrecord HttpServer [config
                       db
                       health-checker
                       maksut-service
                       payment-service
                       email-service
                       mock-dispatcher
                       auth-routes-source]
  component/Lifecycle

  (start [this]
    (s/validate c/MaksutConfig config)
    (s/validate (p/extends-class-pred health/HealthChecker) health-checker)
    (s/validate (p/extends-class-pred auth-routes/AuthRoutesSource) auth-routes-source)
    (let [port   (-> config :server :http :port)
          server (jetty/run-jetty (h/make-handler
                                    (cond-> {:config                 config
                                             :db                     db
                                             :health-checker         health-checker
                                             :maksut-service         maksut-service
                                             :payment-service        payment-service
                                             :email-service          email-service
                                             :auth-routes-source     auth-routes-source}
                                            (some? mock-dispatcher)
                                            (assoc :mock-dispatcher mock-dispatcher)))
                                  {:port         port
                                   ;:host         "0.0.0.0" ; Needed for Windows 127.0.0.1 to work from WSL, might need to remove/configurize for prod
                                   :join?        false
                                   :configurator (fn [server]
                                                   (.setErrorHandler server jetty-error-handler))})]
      (assoc this :server server)))

  (stop [this]
    (when-let [server (:server this)]
      (.stop server))
    (assoc this :server nil)))
