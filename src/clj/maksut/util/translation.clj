(ns maksut.util.translation
  (:require [clojure.string :as str]
            [maksut.translations :as translations]
            [camel-snake-kebab.core :as csk]))

;; Oletusjärjestys, jolla haetaan kielistettyä arvoa eri kielillä
;(def ^:private fi-order [:fi :sv :en])
;(def ^:private sv-order [:sv :fi :en])
;(def ^:private en-order [:en :fi :sv])
;
;(defn- order-for-lang [lang]
;  (case lang
;    :fi fi-order
;    :sv sv-order
;    :en en-order
;    :default fi-order))
;
;(defn get-with-fallback [m lang]
;  (->> (order-for-lang lang)
;       (keep #(get m %))
;       first))


(defn get-translation [lang tx-key]
  (let [[namespace-key name-key] (->> ((juxt namespace name) tx-key)
                                      (map #(-> % csk/->kebab-case keyword)))]
    (-> translations/local-translations namespace-key name-key lang)))

(defn get-translation-ns [lang ns]
  (reduce-kv #(assoc %1 %2 (get %3 lang))
             {}
             (-> translations/local-translations ns)))
