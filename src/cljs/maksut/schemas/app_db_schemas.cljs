(ns maksut.schemas.app-db-schemas
  (:require [clojure.string]
            [schema.core :as s]
            [schema-tools.core :as st]
            [maksut.api-schemas :as api-schemas]))

(s/defschema TutuMaksutPanel
  {:panel      (s/eq :panel/tutu-maksut)
   :parameters {:query {:secret s/Str}
                :path  {}}})

(s/defschema ActivePanel
  {:active-panel
   (s/conditional
     #(-> % :panel (= :panel/tutu-maksut))
     TutuMaksutPanel
     )})

(s/defschema Alert
  {:alert {:message s/Str
           :id      (s/maybe s/Int)}})

(s/defschema Lang
  {:lang (s/enum :fi)})

(s/defschema LocalizedString
  {(s/optional-key :fi) s/Str
   (s/optional-key :sv) s/Str
   (s/optional-key :en) s/Str})

(s/defschema Translation
  {s/Keyword LocalizedString})

(s/defschema Translations
  {:translations {:rest Translation
                  :yleiset Translation}})


(s/defschema Requests
  {:requests #{s/Keyword}})

(s/defschema AppDb
  (st/merge ActivePanel
            Alert
            Lang
            Translations
            Requests))
