(ns maksut.i18n.utils
  (:require [clojure.string :as str]
            [camel-snake-kebab.core :as csk]))

;; Oletusjärjestys, jolla haetaan kielistettyä arvoa eri kielillä
(def ^:private fi-order [:fi :sv :en])
(def ^:private sv-order [:sv :fi :en])
(def ^:private en-order [:en :fi :sv])

(defn- order-for-lang [lang]
  (case lang
    :fi fi-order
    :sv sv-order
    :en en-order
    :default fi-order))

(defn get-with-fallback [m lang]
  (->> (order-for-lang lang)
       (keep #(get m %))
       first))


(defn get-translation [lang translations tx-key]
  (let [[namespace-key name-key] (->> ((juxt namespace name) tx-key)
                                      (map #(-> % csk/->kebab-case keyword)))]
    (-> translations namespace-key name-key lang)))
