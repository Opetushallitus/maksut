(ns maksut.routes
  (:require
            [cheshire.core :as json]
            [clj-ring-db-session.authentication.auth-middleware :as auth-middleware]
            [clj-ring-db-session.session.session-client :as session-client]
            [clj-ring-db-session.session.session-store :refer [create-session-store]]
            [reitit.swagger :as swagger]
            [maksut.api-schemas :as schema]
            [maksut.authentication.auth-routes :as auth-routes]
            [maksut.cas.mock.mock-authenticating-client-schemas :as mock-cas]
            [maksut.cas.mock.mock-dispatcher-protocol :as mock-dispatcher-protocol]
            [maksut.config :as c]
            [maksut.maksut.maksut-service-protocol :as maksut-protocol]
            [maksut.payment.payment-service-protocol :as payment-protocol]
            ;[maksut.email.email-service-protocol :as email-protocol]
            [maksut.health-check :as health-check]
            [maksut.oph-url-properties :as oph-urls]
            [maksut.schemas.class-pred :as p]
            [maksut.session-timeout :as session-timeout]
            [clj-access-logging]
            [ring.middleware.session :as ring-session]
            [ring.util.http-response :as response]
            [schema-tools.core :as st]
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
(defn- random-lowercase-string [n]
  (reduce (fn [acc _] (str acc (char (+ 97 (rand-int 26))))) "" (range n)))

(def ^:private cache-fingerprint (random-lowercase-string 10))

(defn- create-index-handler [config]
  (let [public-config (-> config :public-config json/generate-string)
        rendered-page (selmer/render-file
                       "templates/index.html.template"
                       {:frontend-config   public-config
                        :front-properties  (oph-urls/front-json config)
                        :apply-raamit      (c/production-environment? config)
                        :cache-fingerprint cache-fingerprint})]
    (fn [_]
      (-> (response/ok rendered-page)
          (response/content-type "text/html")
          (response/charset "utf-8")))))


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
(defn- payment-routes [{:keys [mock-dispatcher config payment-service
                               ;email-service
                               ]}]
  ["/payment"
   ;TODO catchaa näissä tapahtuneet exceptionit koska ne näytetään nyt rumasti käyttäjälle
   ;TODO  => ja lisää ?error=error-code
   ["/paytrail"
    ["/success"
    {:get {:parameters {:query schema/TutuPaytrailCallbackRequest}
           :handler    (fn [{{{:as query} :query} :parameters}]
                         (let [params (st/select-schema query schema/PaytrailCallbackRequest)]
                            (payment-protocol/process-success-callback payment-service params false))
                         ;TODO lähetä vain yksi email per prosessoitu maksu
                         ;(email-protocol/send-email email-service "noreply@oph.fi" ["test@test.oph.fi"] "Maksu vastaanotettu" "Maksu vastaanotettu!")
                         (log/warn "Paytrail success " query)
                         ;TODO handle locale (vai pitäiskö se olla tallennettuna jo maksuun?)
                         (response/permanent-redirect (str "/maksut/?secret=" (:tutusecret query))))}}]

    ["/cancel"
     {:get {:parameters {:query schema/TutuPaytrailCallbackRequest}
            :handler (fn [{{{:as query} :query} :parameters}]
                        (log/warn "Paytrail cancel " query)
                        ;Canceling does not really need to be processed in any way, lets just direct back to payment view
                        (response/permanent-redirect (str "/maksut/?secret=" (:tutusecret query) "&payment=cancel"))
                        )}}]

    ["/notify"
     {:get {:parameters {:query schema/TutuPaytrailCallbackRequest}
            :handler (fn [{{{:as query} :query} :parameters}]
                       (log/warn "Paytrail notify " query)
                       (let [params (st/select-schema query schema/PaytrailCallbackRequest)]
                         (payment-protocol/process-success-callback payment-service params true))
                        (response/ok {}))}}]

   ]])

(defn- integration-test-routes [{:keys [mock-dispatcher config]}]
  (when (c/integration-environment? config)
    ["/mock"
     ["/authenticating-client"
      {:post {:summary    "Mockaa yhden CAS-autentikoituvalla clientilla tehdyn HTTP-kutsun"
              :parameters {:body mock-cas/MockCasAuthenticatingClientRequest}
              :handler    (fn [{{spec :body} :parameters}]
                            (.dispatch-mock mock-dispatcher spec)
                            (response/ok {}))}}]
     ["/reset"
      {:post {:summary "Resetoi mockatut HTTP-kutsumääritykset"
              :handler (fn [_]
                         (.reset-mocks mock-dispatcher)
                         (response/ok {}))}}]]))

(defn routes [{:keys [health-checker config db auth-routes-source maksut-service payment-service email-service] :as args}]
  (let [auth (auth-middleware config db)]
    [["/"
      {:get {:no-doc  true
             :handler (fn [_] (response/permanent-redirect "/maksut/"))}}]

     ["/favicon.ico"
      {:get {:no-doc  true
             :handler (fn [_]
                        (-> (response/resource-response "images/rich.jpg" {:root "public"})
                            (response/content-type "image/x-icon")))}}]

     ["/maksut"
      ["/login-error"
       {:get {:no-doc  true
              :handler (create-error-handler config)}}]

      [""
       {:get {:no-doc     true
              :handler    (fn [_] (response/permanent-redirect "/maksut/"))}}]
      ["/"
       {:get {:no-doc     true
              :handler    (create-index-handler config)}}]

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

;For generic (non-TuTu) payments
;       ["/lasku"
;        [""
;         {:post { :middleware auth
;                  :tags       ["Lasku"]
;                  :summary    "Luo uuden laskun"
;                  :responses  {200 {:body schema/Lasku}}
;                  :parameters {:body schema/LaskuCreate}
;                  :handler    (fn [{session :session {lasku :body} :parameters}]
;                                (response/ok (maksut-protocol/create maksut-service session lasku)))}}]
;
;        ]

       ["/lasku-tutu"
        [""
         {:post { :middleware auth
                  :tags       ["Lasku"]
                  :summary    "Luo uuden Tutu laskun"
                  :responses  {200 {:body schema/Lasku}}
                  :parameters {:body schema/TutuLaskuCreate}
                  :handler    (fn [{session :session {lasku :body} :parameters}]
                                (prn "LASKU-TUTU " (type lasku) lasku)
                                (response/ok (maksut-protocol/create-tutu maksut-service session lasku)))}}]

        ]

       ["/lasku-check-tutu"
        [""
         {:post { :middleware auth
                  :tags       ["Lasku"]
                  :summary    "Palauttaa usemman Tutu -laskun statuksen"
                  ;TODO enable this once order-id vs. order_id is refactored everywhere, otherwise this will return status=500
                  ;:responses  {200 {:body [schema/LaskuStatus]}}
                  :parameters {:body schema/LaskuRefList}
                  :handler    (fn [{session :session {input :body} :parameters}]
                                (prn "CHECK-TUTU " (type input) input)
                                (let [x (maksut-protocol/check-status-tutu maksut-service session input)]
                                  (prn "RESULT" x)
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

;For generic (non-TuTu) payments
;       ["/lasku/:order-id"
;        [""
;         {:get {:middleware auth
;                :tags       ["Lasku"]
;                :summary    "Palauttaa olemassa olevan laskun"
;                :responses  {200 {:body schema/Lasku}}
;                :parameters {:path {:order-id s/Str}}
;                :handler    (fn [{session :session {order-id :path} :parameters}]
;                                ;(email-protocol/send-email email-service "noreply@oph.fi" ["test@test.oph.fi"] "Maksu vastaanotettu" "Maksu vastaanotettu!")
;                                (response/ok (maksut-protocol/get-lasku maksut-service session (:order-id order-id))))}}]
;        ]

       ["/lasku/:order-id/maksa"
        [""
         {:get {;No authentication for this service, accessed from /maksut/ Web-page
                 :tags       ["Maksa"]
                 :summary    "Palauttaa form-kentät joilla aloitetaan Paytrail -maksuprosessi"
                 ;:responses  {200 {:body nil}}
                 ;TODO lisää secret tähän ettei voi randomilla maksaa muiden laskuja
                 :parameters {:path {:order-id s/Str}
                              :query {:secret s/Str}}
                 :handler    (fn [{session :session {{:keys [order-id]} :path {:keys [secret]} :query} :parameters}]
                               (log/info "maksa xxx " order-id secret)
                               (response/ok (payment-protocol/tutu-payment payment-service {:order-id order-id
                                                                                            :secret secret}
                                                                                    )))}}]
        ]

       ["/laskut-by-secret"
        [""
         {:get {;No authentication for this service, accessed from /maksut/ Web-page
                 :tags       ["Laskut"]
                 :summary    "Palauttaa laskut salaisuuden perusteella"
                 :responses  {200 {:body [schema/Lasku]}}
                 :parameters {:query {:secret s/Str}}
                 :handler    (fn [{session :session {secret :query} :parameters}]
                               (response/ok (maksut-protocol/get-laskut-by-secret maksut-service session (:secret secret))))}}]
        ]

       (payment-routes args)

       (integration-test-routes args)]
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
