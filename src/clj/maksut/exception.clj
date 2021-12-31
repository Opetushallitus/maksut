(ns maksut.exception
  (:require [reitit.ring.middleware.exception :as ring-exception]
            [ring.util.http-response :as response]
            [taoensso.timbre :as log])
  (:import [clojure.lang ExceptionInfo]))

(defn- json-error-handler [exception request]
  (log/error exception)
  {:status 500
   :headers {"Cache-Control" "no-store"}
   :body (merge {:error true
                 :message (.getMessage exception)}
                (ex-data exception))})

(def exception-middleware
  (ring-exception/create-exception-middleware
    (merge
      ring-exception/default-handlers
      {ExceptionInfo json-error-handler
       Throwable (fn [error _]
                   (log/error error)
                   (response/internal-server-error "Internal Server Error"))
       })))
