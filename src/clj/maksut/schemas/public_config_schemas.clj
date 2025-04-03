(ns maksut.schemas.public-config-schemas
  (:require [schema.core :as s]))

(s/defschema PublicConfig
  {:environment   (s/enum
                    :production
                    :development
                    :it)
   :caller-id     s/Str})
