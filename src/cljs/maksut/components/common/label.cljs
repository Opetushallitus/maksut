(ns maksut.components.common.label
  (:require [maksut.styles.styles-colors :as colors]
            [schema.core :as s]
            [stylefy.core :as stylefy]))


(def ^:private label-styles
  {:color       colors/gray-lighten-1
   :cursor      "pointer"
   :user-select "none"})

(s/defn label
  ([properties]
    (label properties {}))
  ([{:keys [id
           label
           for
           hidden]} :- {(s/optional-key :id)        s/Str
                        :label                      s/Str
                        (s/optional-key :for)       s/Str
                        (s/optional-key :hidden)    s/Bool}
   additional-styles]
  [:label (stylefy/use-style
            (merge label-styles additional-styles)
            {:for       for
             :id        id
             :hidden    hidden})
   label]))
