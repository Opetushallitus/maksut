(ns maksut.components.common.panel
  (:require [maksut.components.common.headings :as h]
            [maksut.styles.styles-colors :as colors]
            [maksut.styles.styles-effects :as effects]
            [maksut.styles.styles-init :as styles-init]
            [stylefy.core :as stylefy]))

(def ^:private main-panel-style
  {:margin-left  "auto"
   :margin-right "auto"
   :max-width    "1000px"
   })

(def ^:private panel-content-style
  {:background-color colors/white
   :filter           effects/drop-shadow-effect-black
   :padding          "15px 25px"

   ;::stylefy/media {styles-init/media-small {
   ;    :background-color "red"
   ;    }}
   })

(defn panel [{:keys [cypressid]} heading contents]
  [:div (stylefy/use-style main-panel-style {:cypressid cypressid})
   [h/heading {:cypressid (str cypressid "-heading")
               :level     :h1}
    heading]
   [:div (stylefy/use-style panel-content-style)
    contents]])

