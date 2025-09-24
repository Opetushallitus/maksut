(ns maksut.routes
  (:require
    [clj-ring-db-session.authentication.auth-middleware :as auth-middleware]
    [clj-ring-db-session.session.session-client :as session-client]
    [clj-ring-db-session.session.session-store :refer [create-session-store]]
    [clojure.string :as str]
    [reitit.swagger :as swagger]
    [maksut.schemas.api-schemas :as schema]
    [maksut.authentication.auth-routes :as auth-routes]
    [maksut.config :as c]
    [maksut.error :refer [maksut-error]]
    [maksut.maksut.maksut-service-protocol :as maksut-protocol]
    [maksut.lokalisaatio.lokalisaatio-service-protocol :as lokalisaatio-protocol]
    [maksut.payment.payment-service-protocol :as payment-protocol]
    [maksut.health-check :as health-check]
    [maksut.oph-url-properties :as oph-urls]
    [maksut.schemas.class-pred :as p]
    [maksut.session-timeout :as session-timeout]
    [maksut.util.url-encoder :refer [encode]]
    [maksut.access-logging :as clj-access-logging]
    [ring.middleware.session :as ring-session]
    [ring.util.http-response :as response]
    [schema.core :as s]
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

; --- Routes ---
(defn- payment-routes [{:keys [payment-service config]}]
  ["/payment"
   ["/paytrail"
    ["/success"
    {:get {:parameters {:query s/Any}
           :handler    (fn [{{{:keys [secret locale] :as query} :query} :parameters}]
                         (log/warn "Paytrail success " query)
                         (try
                           (let [now      (long  (/ (System/currentTimeMillis) 1000))
                                 response (payment-protocol/process-success-callback payment-service
                                                                                     (assoc query :timestamp now)
                                                                                     locale
                                                                                     false)
                                 action   (or (:action response) :error)
                                 uri      (str (get-in config [:urls :oppija-baseurl]) "/" (encode locale) (when (= action :error) "/error") "/?secret=" (encode secret))]
                             (response/permanent-redirect uri))
                           (catch Exception e
                             (log/error "Maksun" query "käsittely epäonnistui:" e)
                             (response/found
                               (str (get-in config [:urls :oppija-baseurl])
                                    "/"
                                    (or (encode locale) "fi")
                                    "/error?secret="
                                    (encode secret))))))}}]

    ["/cancel"
     {:get {:parameters {:query s/Any}
            :handler (fn [{{{:keys [secret locale] :as query} :query} :parameters}]
                        (log/warn "Paytrail cancel " query)
                        ;Canceling does not really need to be processed in any way, lets just direct back to payment view
                        (response/permanent-redirect (str (get-in config [:urls :oppija-baseurl]) "/" (encode locale) "/?secret=" (encode secret) "&payment=cancel"))
                        )}}]

    ["/notify"
     {:get {:parameters {:query s/Any}
            :handler (fn [{{{:keys [locale] :as query} :query} :parameters}]
                       (log/warn "Paytrail notify " query)
                        (payment-protocol/process-success-callback payment-service query locale true)
                        (response/ok {}))}}]

   ]])

(defn routes [{:keys [health-checker config db auth-routes-source maksut-service payment-service lokalisaatio-service] :as args}]
  (let [auth (auth-middleware config db)]
    [["/"
      {:get {:no-doc  true
             :handler (fn [_] (response/permanent-redirect "/maksut/"))}}]

     ["/favicon.ico"
      {:get {:no-doc  true
             :handler (fn [_]
                        (-> (response/resource-response "maksut/images/favicon-32x32.png" {:root "public"})
                            (response/content-type "image/x-icon")))}}]

     ["/maksut"
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
                                (if
                                  (and (= "astu" (:origin lasku))
                                       (or (empty? (get-in lasku [:metadata :form-name]))
                                           (nil? (get-in lasku [:metadata :order-id-prefix]))))
                                  (response/bad-request! "Missing form-name or order-id-prefix for astu invoice")
                                  (response/ok (maksut-protocol/create maksut-service session lasku))))}}]

        ]

       ["/lasku-check"
        [""
         {:post {:middleware auth
                 :tags       ["Lasku"]
                 :summary    "Palauttaa useamman laskun statuksen"
                 :responses  {200 {:body schema/LaskuStatusList}}
                 :parameters {:body schema/LaskuRefList}
                 :handler    (fn [{session :session {input :body} :parameters}]
                               (log/info "Check invoice statuses for keys:" (str/join ", " input))
                               (let [x (maksut-protocol/check-status maksut-service session input)]
                                 (response/ok x)))}}]]

       ["/lasku-invalidate"
        [""
         {:post {:middleware auth
                 :tags       ["Lasku"]
                 :summary    "Mitätöi yhden tai useamman laskun viitenumeron perusteella"
                 :responses  {200 {:body schema/LaskuStatusList}}
                 :parameters {:body schema/LaskuRefList}
                 :handler    (fn [{session :session {input :body} :parameters}]
                               (log/info "Invalidate invoices for" (count input) "keys")
                               (let [resp (maksut-protocol/invalidate-laskut maksut-service session input)]
                                 (response/ok resp)))}}]]

       ["/lasku/:application-key"
        [""
         {:get {:middleware auth
                :tags       ["Lasku"]
                :summary    "Palauttaa kaikki hakemukseen liittyvät laskut"
                :responses  {200 {:body schema/Laskut}}
                :parameters {:path {:application-key s/Str}}
                :handler    (fn [{session :session {input :path} :parameters}]
                              (response/ok (maksut-protocol/list-laskut maksut-service session input)))}}]]

       ["/lasku-tutu"
        [""
         {:post {:middleware auth
                 :tags       ["Lasku"]
                 :summary    "Luo uuden Tutu laskun"
                 :responses  {200 {:body schema/Lasku}}
                 :parameters {:body schema/TutuLaskuCreate}
                 :handler    (fn [{session :session {lasku :body} :parameters}]
                               (response/ok (maksut-protocol/create-tutu maksut-service session lasku)))}}]]

       ["/lasku-check-tutu"
        [""
         {:post { :middleware auth
                  :tags       ["Lasku"]
                  :summary    "Palauttaa usemman Tutu -laskun statuksen"
                  ;:responses  {200 {:body [schema/LaskuStatus]}}
                  :parameters {:body schema/LaskuRefList}
                  :handler    (fn [{session :session {input :body} :parameters}]
                                (log/info "Check invoice statuses for" (count input) "keys")
                                (let [x (maksut-protocol/check-status maksut-service session input)]
                                  (response/ok x)))}}]]

       ["/lasku-tutu/:application-key"
        [""
         {:get {:middleware auth
                :tags       ["Lasku"]
                :summary    "Palauttaa kaikki Tutu-hakemukseen liittyvät laskut"
                :responses  {200 {:body schema/Laskut}}
                :parameters {:path {:application-key s/Str}}
                :handler    (fn [{session :session {input :path} :parameters}]
                              (let [laskut (filter
                                                #(= "tutu" (:origin %))
                                                (maksut-protocol/list-laskut maksut-service session input))]
                                (if (< 0 (count laskut))
                                  (response/ok laskut)
                                  (maksut-error
                                    :invoice-notfound
                                    (str "Laskuja ei löytynyt hakemusavaimella " (:application-key input))))))}}]]

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
                               (try
                                 (let [paytrail-response (payment-protocol/payment payment-service
                                                                                   session
                                                                                   {:order-id order-id
                                                                                    :locale   locale
                                                                                    :secret   secret})]
                                   (response/found (:href paytrail-response)))
                                 (catch Exception e
                                   (log/error "Maksun" order-id "maksaminen epäonnistui:" e)
                                   (response/found
                                     (str (get-in config [:urls :oppija-baseurl])
                                          "/"
                                          (encode (or locale "fi"))
                                          "/error?secret="
                                          (encode secret))))))}}]]

       ["/laskut-by-secret"
        [""
         {:get {;No authentication for this service, accessed from /maksut/ Web-page
                :tags       ["Laskut"]
                :summary    "Palauttaa laskut salaisuuden perusteella"
                :responses  {200 {:body schema/Laskut}}
                :parameters {:query {:secret s/Str}}
                :handler    (fn [{session :session {secret :query} :parameters}]
                              (response/ok (maksut-protocol/get-laskut-by-secret maksut-service session (:secret secret))))}}]]

       ["/lasku-contact"
        [""
         {:get {;No authentication for this service, accessed from /maksut/ Web-page
                :tags       ["Laskut"]
                :summary    "Palauttaa laskun yhteystiedot salaisuuden perusteella"
                :responses  {200 {:body {:contact s/Str}}}
                :parameters {:query {:secret s/Str}}
                :handler    (fn [{session :session {secret :query} :parameters}]
                              (response/ok (maksut-protocol/get-lasku-contact maksut-service session (:secret secret))))}}]]

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
