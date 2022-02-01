(ns maksut.i18n.utils
  (:require [camel-snake-kebab.core :as csk]))

(defn get-translation [lang translations tx-key]
  (let [[namespace-key name-key] (->> ((juxt namespace name) tx-key)
                                      (map #(-> % csk/->kebab-case keyword)))]
    (-> translations namespace-key name-key lang)))
