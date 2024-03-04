(ns maksut.views
  (:require [maksut.components.common.alert :as alert]
            [maksut.views.tutu-maksut-panel :as m]
            [re-frame.core :as re-frame]))

(defn- panels [panel-name]
  (let [panel (case panel-name
                :panel/tutu-maksut [m/tutu-maksut-panel]
                [:div])]
    [:<>
     panel]))

(defn- show-panel [panel-name]
  [panels panel-name])

(defn main-panel []
  [:main
   [alert/alert]
   (let [{panel :panel} @(re-frame/subscribe [:panel/active-panel])]
     [show-panel panel])])
