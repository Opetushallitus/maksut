(ns maksut.util.translation
  (:require [clojure.string :as str]
            [maksut.translations :as translations]
            [camel-snake-kebab.core :as csk]))


(defn get-translation [lang tx-key]
  (let [[namespace-key name-key] (->> ((juxt namespace name) tx-key)
                                      (map #(-> % csk/->kebab-case keyword)))]
    (-> translations/local-translations namespace-key name-key lang)))

(defn get-translation-ns [lang ns]
  (reduce-kv #(assoc %1 %2 (get %3 lang))
             {}
             (-> translations/local-translations ns)))
