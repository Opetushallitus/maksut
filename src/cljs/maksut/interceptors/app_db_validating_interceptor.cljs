(ns maksut.interceptors.app-db-validating-interceptor
  (:require ;[maksut.schemas.app-db-schemas :as schema]
            [re-frame.std-interceptors :as interceptors]
            ;[schema.core :as s]
            ))

(defn- validate-app-db [db]
  ;(s/validate schema/AppDb db)
  db
  )

(def validate-interceptor (interceptors/after validate-app-db))
