(ns maksut.files.filesystem-store
  (:require [maksut.files.file-store :as file-store]
            [clojure.java.io :as io]))

(defrecord ReceiptFilesystemStore [config]
  file-store/ReceiptStorageEngine

  (create-file-from-bytearray [_ file-bytes file-key]
    (let [base-path (get-in config [:file-store :filesystem :base-path])
          dir       (io/file base-path)
          dest-file (io/file (str base-path "/" file-key))]
      (.mkdirs dir)
      (io/copy file-bytes dest-file)))

  (get-file [_ file-key]
    (let [base-path (get-in config [:file-store :filesystem :base-path])
          path      (str base-path "/" file-key)]
      (io/input-stream (io/file path)))))

(defn new-store []
  (map->ReceiptFilesystemStore {}))
