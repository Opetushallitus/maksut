(ns maksut.cas.mock.mock-authenticating-client-schemas
  (:require [schema.core :as s]))

(s/defschema MockCasAuthenticatingClientRequest
  {:method                   (s/enum :post :get :put :delete)
   :path                     s/Str
   ;:service                  (s/enum )
   (s/optional-key :request) s/Any
   :response                 s/Any})
