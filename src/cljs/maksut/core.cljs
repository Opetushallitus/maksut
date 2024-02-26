(ns maksut.core
  (:require
    [reagent.dom :as reagent]
    [re-frame.core :as re-frame]
    [maksut.events.core-events]
    [maksut.events.alert-events]
    [maksut.events.http-events]
    [maksut.events.panel-events]
    [maksut.fx.dispatch-debounced-fx]
    [maksut.fx.http-fx]
    [maksut.routes :as routes]
    [maksut.views :as views]
    [maksut.styles.styles-init :as styles]
    [maksut.subs.core-subs]
    [maksut.subs.alert-subs]
    [maksut.subs.panel-subs]))

(defn ^:dev/after-load mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn init []
  (styles/init-styles)
  (re-frame/dispatch-sync [:core/initialize-db])
  (routes/app-routes)
  (mount-root))
