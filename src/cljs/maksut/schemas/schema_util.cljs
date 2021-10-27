(ns maksut.schemas.schema-util
  (:require ;[taoensso.timbre :as log]
            [schema-tools.coerce :as scr]
            [schema.coerce :as sc])
  ;(:import [java.time LocalDateTime])
  ;(:import [java.time.format DateTimeParseException])
    )


;(defn parse-local-date-time [date-string]
;  (try
;    (LocalDateTime/parse date-string)
;    (catch DateTimeParseException _
;      (log/warn "Cannot parse java.time.LocalDateTime from input [" date-string "]")
;      date-string)))

;(def +extended-json-coercions+
;  (assoc scr/+json-coercions+ LocalDateTime parse-local-date-time))

(def extended-json-coercion-matcher
  (some-fn
    scr/+json-coercions+
    sc/keyword-enum-matcher
    sc/set-matcher))
