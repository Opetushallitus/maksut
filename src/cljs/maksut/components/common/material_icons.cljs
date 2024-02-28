(ns maksut.components.common.material-icons
  (:require [stylefy.core :as stylefy]))

(def ^:private material-icon-styles
  {:font-family             "Material Icons"
   :font-feature-settings   "liga"
   :font-weight             "normal"
   :font-style              "normal"
   :font-size               "16px"
   :display                 "inline-block"
   :line-height             1
   :text-transform          "none"
   :letter-spacing          "normal"
   :text-rendering          "optimizeLegibility"
   :word-wrap               "normal"
   :white-space             "nowrap"
   :direction               "ltr"
   :-webkit-font-smoothing  "antialiased"
   :-moz-osx-font-smoothing "grayscale"})

(defn- material-icon [icon additional-styles options]
  (let [ styles (merge material-icon-styles additional-styles)]
    [:i (stylefy/use-style styles options)
     icon]))

(defn trending_flat []
  [material-icon "trending_flat" {}])

(defn done-bold []
  [material-icon "done" {}])
