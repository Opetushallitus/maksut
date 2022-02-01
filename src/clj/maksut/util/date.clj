(ns maksut.util.date
  (:require [clj-time.format :as format]
            [clojure.string :as string]))

(defonce date-formatter (format/formatters :date))
(defonce fi-formatter (format/formatter "dd.MM.yyyy"))

(defn iso-date-str->date [date-str]
  (when-not (string/blank? date-str)
            (try
              (format/parse-local-date date-formatter date-str)
              (catch Exception _))))

(defn format-date [iso-date-str]
  (when-let [date (iso-date-str->date iso-date-str)]
    (format/unparse fi-formatter date)))
