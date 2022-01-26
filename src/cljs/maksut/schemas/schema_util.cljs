(ns maksut.schemas.schema-util
  (:require [schema-tools.coerce :as scr]
            [schema.coerce :as sc]))

(def extended-json-coercion-matcher
  (some-fn
    scr/+json-coercions+
    sc/keyword-enum-matcher
    sc/set-matcher))
