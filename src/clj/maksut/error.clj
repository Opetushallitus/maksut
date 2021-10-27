(ns maksut.error
  (:require [taoensso.timbre :as log]))

(defn maksut-error [code msg]
  (throw (ex-info (or msg "Maksut operation failed") { :type :maksut.error
                                                       :code (or code :maksut-errorcode-missing)})))
