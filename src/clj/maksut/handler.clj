(ns maksut.handler
  (:require [reitit.swagger :as swagger]
            [reitit.swagger-ui :as swagger-ui]
            [reitit.coercion.schema]
            [reitit.dev.pretty :as pretty]
            [reitit.ring.middleware.muuntaja :as muuntaja-middleware]
            [reitit.ring.middleware.parameters :as parameters-middleware]
            [reitit.ring.coercion :as coercion]
            [reitit.ring :as ring]
            [environ.core :refer [env]]
            [maksut.routes :refer [routes]]
            [maksut.authentication.auth-routes :as auth-routes]
            [maksut.config :as c]
            [maksut.exception :as exception]
            [maksut.maksut.maksut-service-protocol :as maksut-protocol]
            [maksut.payment.payment-service-protocol :as payment-protocol]
            [maksut.email.email-service-protocol :as email-protocol]
            [maksut.lokalisaatio.lokalisaatio-service-protocol :as lokalisaatio-protocol]
            [maksut.health-check :as health-check]
            [maksut.schemas.class-pred :as p]
            [maksut.util.cache-control :as cache-control]
            [maksut.logs.access-logging :as clj-access-logging]
            [maksut.logs.stdout-access-logging :as stdout-access-logging]
            [maksut.logs.timbre-access-logging :as timbre-access-logging]
            [ring.middleware.defaults :as defaults]
            [ring.middleware.json :as wrap-json]
            [ring.middleware.reload :as reload]
            [ring.util.http-response :as response]
            [schema.core :as s]
            [muuntaja.core :as m])
  (:import [javax.sql DataSource]))



(s/defschema MakeHandlerArgs
  {:config                           c/MaksutConfig
   :db                               {:datasource (s/pred #(instance? DataSource %))
                                      :config     c/MaksutConfig}
   :health-checker                   (p/extends-class-pred health-check/HealthChecker)
   :auth-routes-source               (p/extends-class-pred auth-routes/AuthRoutesSource)
   :maksut-service                   (p/extends-class-pred maksut-protocol/MaksutServiceProtocol)
   :payment-service                  (p/extends-class-pred payment-protocol/PaymentServiceProtocol)
   :email-service                    (p/extends-class-pred email-protocol/EmailServiceProtocol)
   :lokalisaatio-service             (p/extends-class-pred lokalisaatio-protocol/LokalisaatioServiceProtocol)})



;Should be Str->BigDecimal but Muuntaja only supports Float->BigDecimal conversion
(def new-muuntaja-instance
  (m/create
   (assoc-in
      m/default-options
      [:formats "application/json" :decoder-opts :bigdecimals]
      true)))

(defn- wrap-referrer-policy
  [handler policy]
  (fn [request]
    (response/header (handler request) "Referrer-Policy" policy)))

(defn router [args]
  (ring/router
    (routes args)
    {:exception pretty/exception
     :data      {:coercion   reitit.coercion.schema/coercion
                 :muuntaja   new-muuntaja-instance
                 :middleware [swagger/swagger-feature
                              parameters-middleware/parameters-middleware
                              muuntaja-middleware/format-negotiate-middleware
                              muuntaja-middleware/format-response-middleware
                              exception/exception-middleware
                              muuntaja-middleware/format-request-middleware
                              coercion/coerce-response-middleware
                              coercion/coerce-request-middleware]}}))

(s/defn create-handler [args :- MakeHandlerArgs]
  (ring/ring-handler
    (router args)
    (ring/routes
      (swagger-ui/create-swagger-ui-handler
        {:config {:validatorUrl     nil
                  :operationsSorter "alpha"}
         :path   "/maksut/swagger"
         :url    "/maksut/swagger.json"})
      (ring/create-resource-handler {:path "/maksut" :root "public/maksut"})
      (ring/create-default-handler {:not-found (constantly {:status 404, :body "<h1>Not found</h1>"})}))))

(def reloader #'reload/reloader)

(defn wrapper-if
  [f enable? middleware]
  (let [wrapped (middleware f)]
    (fn [request]
      (if enable?
          (wrapped request)
          (f request)))))

(s/defn make-production-handler
  [{config :config :as args} :- MakeHandlerArgs]
  (-> (create-handler args)
      (clj-access-logging/wrap-access-logging)
      (wrapper-if (not (c/production-environment? config))
        stdout-access-logging/wrap-stdout-access-logging)
      (timbre-access-logging/wrap-timbre-access-logging
        {:path (str (-> args :config :log :base-path)
                    "/access_maksut"
                    (when (:hostname env) (str "_" (:hostname env))))})
      (wrap-json/wrap-json-response)
      (wrap-referrer-policy "no-referrer")
      (cache-control/wrap-cache-control)
      (defaults/wrap-defaults (-> defaults/site-defaults
                                  (dissoc :static)
                                  (update :security dissoc :anti-forgery)))))

(s/defn make-reloading-handler
  [args :- MakeHandlerArgs]
  (let [reload (reloader ["src/clj" "src/cljc"] true)]
    (fn [request]
      (reload)
      (let [handler (make-production-handler args)]
        (handler request)))))

(s/defn make-handler
  [{config :config :as args} :- MakeHandlerArgs]
  (if (c/production-environment? config)
    (make-production-handler args)
    (make-reloading-handler args)))
