(ns maksut.util.url-encoder
    (:import [org.apache.commons.codec.net URLCodec]))

(def ^:private encoder (new URLCodec))

(defn encode [str]
  (.encode encoder str))
