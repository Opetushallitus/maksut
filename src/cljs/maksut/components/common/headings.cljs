(ns maksut.components.common.headings
  (:require [maksut.styles.styles-colors :as colors]
            [maksut.styles.styles-fonts :as vars]
            [maksut.styles.styles-init :refer [media-small]]
            [stylefy.core :as stylefy]))

(def h1-styles
  {:color       colors/black
   :font-size   "48px"
   ::stylefy/media {media-small { :font-size "28px" }}
   :font-weight vars/font-weight-bold
   :text-align "center"
   :line-height "24px"})

(def h2-styles
  {:color       colors/black
   :font-size   "28px"
   ::stylefy/media {media-small { :font-size "24px" }}
   :font-weight vars/font-weight-bold
   :line-height "24px"})

(def h3-styles
  {:color       colors/black
   :font-size   "22px"
   :font-weight vars/font-weight-bold
   :line-height "24px"})

(def h4-styles
  {:color       colors/black
   :font-size   "18px"
   :font-weight vars/font-weight-regular
   :line-height "24px"})

(defn heading [{:keys [cypressid
                       level
                       style
                       id]} heading-text]
  (let [[element styles] (case level
                           :h1 [:h1 h1-styles]
                           :h2 [:h2 h2-styles]
                           :h3 [:h3 h3-styles]
                           :h4 [:h3 h4-styles])]
    [element (stylefy/use-style
               (merge styles style)
               {:id        id
                :cypressid cypressid})
     heading-text]))
