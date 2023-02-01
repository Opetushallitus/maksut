(ns maksut.files.file-store)

(defprotocol StorageEngine
  (create-file-from-bytearray [this file-bytes file-key])

  (get-file [this file-key]))