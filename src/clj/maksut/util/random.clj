(ns maksut.util.random
    (:refer-clojure :exclude [bytes])
    (:require [clojure.string :as string])
    (:import java.security.SecureRandom
             [org.apache.commons.codec.binary Base64]))

(defn- bytes [size]
  (let [seed (byte-array size)]
    (.nextBytes (SecureRandom.) seed)
    seed))

(defn- base64 [size]
  (String. (Base64/encodeBase64 (bytes size))))

(defn- urlify [str]
  (-> str
      (string/replace "+" "-")
      (string/replace "/" "_")
      (string/replace "=" "")))

(defn url-part [size]
  (urlify (base64 size)))

(defn base64-string [str]
  (urlify (String. (Base64/encodeBase64 (.getBytes str)))))
