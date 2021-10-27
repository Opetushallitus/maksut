(ns maksut.routes
  (:require [camel-snake-kebab.core :as csk]
            [camel-snake-kebab.extras :as cske]
            [maksut.config :as c]
            [reitit.coercion.spec :as rss]
            [reitit.frontend :as rf]
            [reitit.frontend.easy :as rfe]
            [spec-tools.data-spec :as ds]
            [re-frame.core :as re-frame]))

(def default-panel (:default-panel c/config))

(def routes2
  [["/"
    {:redirect :panel/tutu-maksut}]
   ;{:redirect default-panel}]
   ;["/maksut"
   ; {:redirect default-panel}]
   ;["/maksut/"
   ; {:redirect default-panel}]
   ;["/maksut/"
   ; {:redirect :panel/tutu-maksut}]
   ["/maksut/"
    {:name :panel/tutu-maksut
    :parameters {:query {:secret string?}}}]
   ["/maksut/tutu"
    {:name :panel/tutu-maksut
     :parameters {:query {:secret string?}}}]])

(def routes
  [;["/"
   ; {:redirect :panel/tutu-maksut}]
   ["/maksut/"
    {:name :panel/tutu-maksut
     :parameters {:query {:secret string?
                          (ds/opt :payment) string?
                          }}}]
    ])

(def keys->kebab-case (partial cske/transform-keys csk/->kebab-case-keyword))

(defn app-routes []
  (rfe/start!
    (rf/router
      routes
      {:data {:coercion rss/coercion}})
    (fn [m]
      (let [{{:keys [name redirect]}    :data
             {:keys [path query]
              :or   {path {} query {}}} :parameters}
            m]
        (prn "NAVIGATE1 " name redirect "path" path "query" query) ; @redirect @path @query)
        ;(prn "NAVIGATE2 " path js->clj pr-str) (-> query js->clj pr-str))


        ;(re-frame/dispatch [:panel/set-active-panel
        ;                    {:panel      name
        ;                     :parameters {:path  (keys->kebab-case path)
        ;                                  :query (keys->kebab-case query)}}])

        ;(comment
          (cond redirect
              (rfe/replace-state redirect)

              name
              (re-frame/dispatch [:panel/set-active-panel
                                  {:panel      name
                                   :parameters {:path  (keys->kebab-case path)
                                                :query (keys->kebab-case query)}}]))
         ;)

        ))
    {:use-fragment false}))
