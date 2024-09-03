(ns maksut.routes
  (:require
            [cheshire.core :as json]
            [clj-ring-db-session.authentication.auth-middleware :as auth-middleware]
            [clj-ring-db-session.session.session-client :as session-client]
            [clj-ring-db-session.session.session-store :refer [create-session-store]]
            [reitit.swagger :as swagger]
            [maksut.api-schemas :as schema]
            [maksut.authentication.auth-routes :as auth-routes]
            [maksut.config :as c]
            [maksut.maksut.maksut-service-protocol :as maksut-protocol]
            [maksut.lokalisaatio.lokalisaatio-service-protocol :as lokalisaatio-protocol]
            [maksut.payment.payment-service-protocol :as payment-protocol]
            [maksut.health-check :as health-check]
            [maksut.oph-url-properties :as oph-urls]
            [maksut.schemas.class-pred :as p]
            [maksut.session-timeout :as session-timeout]
            [maksut.util.url-encoder :refer [encode]]
            [clj-access-logging]
            [ring.middleware.session :as ring-session]
            [ring.util.http-response :as response]
            [schema.core :as s]
            [selmer.parser :as selmer]
            [taoensso.timbre :as log]))

; --- Session ---
(defn- create-wrap-database-backed-session [config datasource]
  (fn [handler] (ring-session/wrap-session handler
                                           {:root         "/maksut"
                                            :cookie-attrs {:secure (c/production-environment? config)}
                                            :store        (create-session-store datasource)})))

(defn auth-middleware [config db]
  [(create-wrap-database-backed-session config (:datasource db))
   clj-access-logging/wrap-session-access-logging
   #(auth-middleware/with-authentication % (oph-urls/resolve-url :cas.login config))
   session-client/wrap-session-client-headers
   (session-timeout/create-wrap-absolute-session-timeout config)])

; --- Handlers ---
(defn- create-error-handler [config]
  (let [rendered-page (selmer/render-file
                       "templates/login-error.html.template"
                       {:apply-raamit (c/production-environment? config)})]
    (fn [_]
      (log/warn "Kirjautuminen epäonnistui ja käyttäjä ohjattiin virhesivulle.")
      (-> (response/forbidden rendered-page)
          (response/content-type "text/html")
          (response/charset "utf-8")))))


; --- Routes ---
(defn- payment-routes [{:keys [payment-service config]}]
  ["/payment"
   ["/paytrail"
    ["/success"
    {:get {:parameters {:query s/Any}
           :handler    (fn [{{{:keys [tutusecret tutulocale] :as query} :query} :parameters}]
                         (log/warn "Paytrail success " query)
                         (let [now      (long  (/ (System/currentTimeMillis) 1000))
                               response (payment-protocol/process-success-callback payment-service
                                                                                   (assoc query :timestamp now)
                                                                                   tutulocale
                                                                                   false)
                               action   (or (:action response) :error)
                               uri-end  (if (= action :error) "&payment=error" "")
                               uri      (str (get-in config [:urls :oppija-baseurl]) "/?secret=" (encode tutusecret) "&locale=" (encode tutulocale) uri-end)]
                              (response/permanent-redirect uri)))}}]

    ["/cancel"
     {:get {:parameters {:query s/Any}
            :handler (fn [{{{:keys [tutusecret tutulocale] :as query} :query} :parameters}]
                        (log/warn "Paytrail cancel " query)
                        ;Canceling does not really need to be processed in any way, lets just direct back to payment view
                        (response/permanent-redirect (str (get-in config [:urls :oppija-baseurl]) "/?secret=" (encode tutusecret) "&locale=" (encode tutulocale) "&payment=cancel"))
                        )}}]

    ["/notify"
     {:get {:parameters {:query s/Any}
            :handler (fn [{{{:keys [tutulocale] :as query} :query} :parameters}]
                       (log/warn "Paytrail notify " query)
                        (payment-protocol/process-success-callback payment-service query tutulocale true)
                        (response/ok {}))}}]

   ]])

(defn routes [{:keys [health-checker config db auth-routes-source maksut-service payment-service lokalisaatio-service] :as args}]
  (let [auth (auth-middleware config db)]
    [["/"
      {:get {:no-doc  true
             :handler (fn [_] (response/permanent-redirect "/maksut/"))}}]

     ["/maksut"
      ["/login-error"
       {:get {:no-doc  true
              :handler (create-error-handler config)}}]

      ["/swagger.json"
       {:get {:no-doc  true
              :swagger {:info {:title       "Maksut"
                               :description "Maksut ulkoinen rajapinta."}}
              :handler (swagger/create-swagger-handler)}}]
      ["/api"
       ["/health"
        {:get {:summary "Health Check"
               :tags    ["Admin"]
               :handler (fn [_]
                          (s/validate (p/extends-class-pred health-check/HealthChecker) health-checker)
                          (-> (health-check/check-health health-checker)
                              response/ok
                              (response/content-type "text/html")))}}]

       ["/localisation/:locale"
        {:get {:summary "Get localisations from localisation service"
               :tags ["Localisation"]
               :parameters {:path {:locale schema/Locale}}
               :handler (fn [{{input :path} :parameters}]
                          (response/ok (lokalisaatio-protocol/get-localisations lokalisaatio-service (:locale input))))}}]

       ["/lasku"
        [""
         {:post { :middleware auth
                  :tags       ["Lasku"]
                  :summary    "Luo uuden laskun"
                  :responses  {200 {:body schema/Lasku}}
                  :parameters {:body schema/LaskuCreate}
                  :handler    (fn [{session :session {lasku :body} :parameters}]
                                (response/ok (maksut-protocol/create maksut-service session lasku)))}}]

        ]

       ["/lasku-check"
        [""
         {:post { :middleware auth
                 :tags       ["Lasku"]
                 :summary    "Palauttaa useamman laskun statuksen"
                 ;:responses  {200 {:body [schema/LaskuStatus]}}
                 :parameters {:body schema/LaskuRefListWithOrigin}
                 :handler    (fn [{session :session {input :body} :parameters}]
                               (log/info "Check invoice statuses for" (count input) "keys")
                               (let [x (maksut-protocol/check-status maksut-service session input)]
                                 (response/ok x)))}}]]

       ["/lasku/:application-key/:origin"
        [""
         {:get {:middleware auth
                :tags       ["Lasku"]
                :summary    "Palauttaa kaikki hakemus-origin pariin liittyvät laskut"
                :responses  {200 {:body schema/Laskut}}
                :parameters {:path {:application-key s/Str
                                    :origin s/Str}}
                :handler    (fn [{session :session {input :path} :parameters}]
                              (response/ok (maksut-protocol/list maksut-service session input)))}}]]

       ["/lasku-tutu"
        [""
         {:post { :middleware auth
                  :tags       ["Lasku"]
                  :summary    "Luo uuden Tutu laskun"
                  :responses  {200 {:body schema/Lasku}}
                  :parameters {:body schema/TutuLaskuCreate}
                  :handler    (fn [{session :session {lasku :body} :parameters}]
                                (response/ok (maksut-protocol/create-tutu maksut-service session lasku)))}}]

        ]

       ["/lasku-check-tutu"
        [""
         {:post { :middleware auth
                  :tags       ["Lasku"]
                  :summary    "Palauttaa usemman Tutu -laskun statuksen"
                  ;:responses  {200 {:body [schema/LaskuStatus]}}
                  :parameters {:body schema/LaskuRefList}
                  :handler    (fn [{session :session {input :body} :parameters}]
                                (log/info "Check invoice statuses for" (count input) "keys")
                                (let [x (maksut-protocol/check-status-tutu maksut-service session input)]
                                  (response/ok x)))}}]]

       ["/lasku-tutu/:application-key"
        [""
         {:get {:middleware auth
                :tags       ["Lasku"]
                :summary    "Palauttaa kaikki Tutu-hakemukseen liittyvät laskut"
                :responses  {200 {:body schema/Laskut}}
                :parameters {:path {:application-key s/Str}}
                :handler    (fn [{session :session {input :path} :parameters}]
                              (response/ok (maksut-protocol/list-tutu maksut-service session input)))}}]]

       ["/lasku/:order-id/maksa"
        [""
         {:get {;No authentication for this service, accessed from /maksut/ Web-page
                 :tags       ["Maksa"]
                 :summary    "Palauttaa form-kentät joilla aloitetaan Paytrail -maksuprosessi"
                 ;:responses  {200 {:body nil}}
                 :parameters {:path {:order-id s/Str}
                              :query {:secret s/Str
                                      (s/optional-key :locale) (s/maybe schema/Locale)}}
                 :handler    (fn [{session :session {{:keys [order-id]} :path {:keys [secret locale]} :query} :parameters}]
                               (log/info "Generate Paytrail form fields for " order-id locale secret)
                               (let [paytrail-response (payment-protocol/tutu-payment payment-service
                                                                                      session
                                                                                      {:order-id order-id
                                                                                       :locale   locale
                                                                                       :secret   secret})]
                                 (response/found (:href paytrail-response))))}}]]

       ["/laskut-by-secret"
        [""
         {:get {;No authentication for this service, accessed from /maksut/ Web-page
                 :tags       ["Laskut"]
                 :summary    "Palauttaa laskut salaisuuden perusteella"
                 :responses  {200 {:body [schema/Lasku]}}
                 :parameters {:query {:secret s/Str}}
                 :handler    (fn [{session :session {secret :query} :parameters}]
                               (response/ok (maksut-protocol/get-laskut-by-secret maksut-service session (:secret secret))))}}]]

       ["/kuitti/:file-key"
        [""
         {:get {:middleware auth
                :tags       ["Kuitti"]
                :summary    "Palauttaa maksun kuitin"
                :responses  {200 {:body s/Any}}
                :parameters {:path {:file-key s/Str}}
                :handler    (fn [{session :session {input :path} :parameters}]
                              (if-let [file-response (payment-protocol/get-kuitti payment-service session input)]
                                (-> (response/ok file-response)
                                    (assoc "Content-Disposition"
                                           (str "attachment; filename=\"" (:filename file-response) "\"")))
                                (response/not-found)))}}]]

       (payment-routes args)]
      ["/auth"
       {:middleware (conj auth session-client/wrap-session-client-headers)}
       ["/cas"
        {:get  {:no-doc     true
                :parameters {:query {:ticket s/Str}}
                :handler    (fn [{{{:keys [ticket]} :query} :parameters :as request}]
                              (auth-routes/login auth-routes-source ticket request))}
         :post {:no-doc     true
                :parameters {:form {:logoutRequest s/Str}}
                :handler    (fn [{{logout-request :logoutRequest} :params}]
                              (auth-routes/cas-logout auth-routes-source logout-request))}}]
       ["/logout"
        {:get {:no-doc  true
               :handler (fn [{:keys [session]}] (auth-routes/logout auth-routes-source session))}}]]]]))
