(ns maksut.error)

(defn maksut-error [code msg &{:keys [status-code]}]
  (throw
    (ex-info (or msg "Maksut operation failed")
             (-> {:type :maksut.error
                  :code (or code :maksut-errorcode-missing)}
                 (cond-> status-code (assoc :http-status status-code))))))
