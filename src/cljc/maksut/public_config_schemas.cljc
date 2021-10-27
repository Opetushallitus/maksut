(ns maksut.public-config-schemas
  (:require [schema.core :as s]))

(s/defschema PublicConfig
  {:environment   (s/enum
                    :production
                    :development
                    :it)
   :default-panel (s/enum
                   :panel/tutu-maksut)
   :caller-id     s/Str})
