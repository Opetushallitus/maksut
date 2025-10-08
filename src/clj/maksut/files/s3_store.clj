(ns maksut.files.s3-store
  (:require [maksut.files.file-store :as file-store])
  (:import (software.amazon.awssdk.core.sync RequestBody)
           (software.amazon.awssdk.services.s3 S3Client)
           (software.amazon.awssdk.services.s3.model GetObjectRequest PutObjectRequest)))

(defn- bucket-name [config]
  (get-in config [:file-store :s3 :bucket]))

(defn- request-body ^RequestBody [file-bytes]
  (RequestBody/fromBytes file-bytes))

(defn- put-request ^PutObjectRequest [bucket key]
  (-> (PutObjectRequest/builder)
      (.bucket bucket)
      (.key key)
      (.build)))

(defn- get-request ^GetObjectRequest [bucket key]
  (-> (GetObjectRequest/builder)
      (.bucket bucket)
      (.key key)
      (.build)))

(defrecord ReceiptS3Store [^{:s3-client S3Client} s3-client config]
  file-store/ReceiptStorageEngine

  (create-file-from-bytearray [_ file-bytes file-key]
    (let [request (put-request (bucket-name config) file-key)
          body (request-body file-bytes)
          client ^S3Client (:s3-client s3-client)]
      (.putObject client request body)))

  (get-file [_ file-key]
    (let [request (get-request (bucket-name config) file-key)
          client ^S3Client (:s3-client s3-client)]
      (.getObject client request))))

(defn new-store []
  (map->ReceiptS3Store {}))
