(ns maksut.files.s3-store
  (:require [maksut.files.file-store :as file-store])
  (:import (java.io ByteArrayInputStream)
           (com.amazonaws.services.s3.model ObjectMetadata)))

(defn- bucket-name [config]
  (get-in config [:file-store :s3 :bucket]))

(defrecord ReceiptS3Store [s3-client config]
  file-store/ReceiptStorageEngine

  (create-file-from-bytearray [_ file-bytes file-key]
    (with-open [inputstream (ByteArrayInputStream. file-bytes)]
      (let [s3-metadata (ObjectMetadata.)]
        (.setContentLength s3-metadata (count file-bytes))
        (.putObject (:s3-client s3-client) (bucket-name config) file-key inputstream s3-metadata))))

  (get-file [_ file-key]
    (-> (.getObject (:s3-client s3-client) (bucket-name config) file-key)
        (.getObjectContent))))

(defn new-store []
  (map->ReceiptS3Store {}))
