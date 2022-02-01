(ns maksut.dates.date-parser
  (:require [cljs-time.format :as f]
            [clojure.string :as string]))

(defonce date-formatter (f/formatters :date))
(defonce fi-formatter (f/formatter "dd.MM.yyyy"))

(defn iso-date-str->date [date-str]
  (when-not (string/blank? date-str)
    (try
      (f/parse-local-date date-formatter date-str)
      (catch js/Error _))))

(defn format-date [iso-date-str]
  (when-let [date (iso-date-str->date iso-date-str)]
    (f/unparse fi-formatter date)))
