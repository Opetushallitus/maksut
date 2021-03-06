(ns maksut.util.cache-control
  (:require
   [clojure.string :as string]
   [ring.util.response :as response]))

(def one-month-in-seconds (* 60 60 24 30))
(def cache-for-a-long-time (str "public, max-age=" one-month-in-seconds))
(def do-not-cache-at-all "no-store")
(def resource-suffixes-to-cache ["css" "js" "jpg" "jpeg" "png" "woff" "woff2" "ico"])

(defn is-resource [uri]
  (some true? (map #(string/ends-with? uri (str "." %)) resource-suffixes-to-cache)))

(defn wrap-cache-control [handler]
  (fn [req]
    (let [resp  (handler req)
          uri   (:uri req)
          cache (if (is-resource uri) cache-for-a-long-time do-not-cache-at-all)]
      (if (some? (response/get-header resp "Cache-Control"))
        resp
        (response/header resp "Cache-Control" cache)))))
