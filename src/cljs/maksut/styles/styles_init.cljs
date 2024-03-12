(ns maksut.styles.styles-init
  (:require [goog.string :as gstring]
            [stylefy.core :as stylefy]
            [stylefy.reagent :as stylefy-reagent]
            [maksut.styles.styles-colors :as colors]
            [maksut.config :as c]
            [maksut.styles.styles-fonts :as vars]))

(def small-width "768px")
(def media-small {:max-width small-width})

(def ^:private body-styles
  {:background-color colors/gray-lighten-5
   :font-family      vars/font-family
   :font-size        "16px"
   :font-weight      vars/font-weight-regular
   :line-height      "24px"

   ::stylefy/media {media-small {
             :margin-left "0"
             :margin-right "0"}}
})

(def ^:private input-styles
  {::stylefy/mode [[:disabled {:background-color "inherit"}]]})

(defn- add-font-styles []
  (doseq [format ["woff" "woff2"]]
    (doseq [weight [vars/font-weight-regular
                    vars/font-weight-medium
                    vars/font-weight-bold]]
      (stylefy/font-face {:font-family "Material Icons"
                          :src         (gstring/format "url('/maksut/fonts/MaterialIcons-Regular.%s') format('%s')" format format)
                          :font-weight weight
                          :font-style  "normal"}))))

(defn init-styles []
  (stylefy/init {:use-caching? (-> c/config :environment (= :production))
                 :dom (stylefy-reagent/init)})
  (stylefy/tag "body" body-styles)
  (stylefy/tag "input" input-styles)
  (add-font-styles))
