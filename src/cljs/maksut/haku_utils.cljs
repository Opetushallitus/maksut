(ns maksut.haku-utils
  (:require [maksut.i18n.utils :as i18n-utils]
            [cljs-time.format :as f]
            [clojure.string :as str]))

(defonce date-parser (f/formatters :date))
(defonce date-formatter (f/formatter "dd.MM.yyyy"))

(defn iso-date-str->date [date-str]
  (when-not (str/blank? date-str)
            (try
              (f/parse-local date-formatter date-str)
              (catch js/Error _))))

(defn format-iso-date [date-str]
   (f/unparse date-formatter (iso-date-str->date date-str)))


(defn- includes-string? [m string lang]
  (-> (i18n-utils/get-with-fallback m lang)
      str/lower-case
      (str/includes? string)))


