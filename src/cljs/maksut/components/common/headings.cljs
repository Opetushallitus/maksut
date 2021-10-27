(ns maksut.components.common.headings
  (:require [maksut.styles.styles-colors :as colors]
            [maksut.styles.styles-fonts :as vars]
            [stylefy.core :as stylefy]))

(def h1-styles
  {:color       colors/black
   :font-size   "48px"
   :font-weight vars/font-weight-bold
   :text-align "center" ;this should be somewhere else
   :line-height "24px"})

(def h2-styles
  {:color       colors/black
   :font-size   "24px"
   :font-weight vars/font-weight-bold
   :line-height "24px"})

(def h3-styles
  {:color       colors/black
   :font-size   "20px"
   :font-weight vars/font-weight-bold
   :line-height "24px"})

(def h4-styles
  {:color       colors/black
   :font-size   "18px"
   :font-weight vars/font-weight-regular
   :line-height "24px"})

(defn heading [{:keys [cypressid
                       level
                       id]} heading-text]
  (let [[element styles] (case level
                           :h1 [:h1 h1-styles]
                           :h2 [:h2 h2-styles]
                           :h3 [:h3 h3-styles]
                           :h4 [:h3 h4-styles])]
    [element (stylefy/use-style
               styles
               {:id        id
                :cypressid cypressid})
     heading-text]))
