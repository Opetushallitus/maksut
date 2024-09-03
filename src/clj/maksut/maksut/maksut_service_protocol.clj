(ns maksut.maksut.maksut-service-protocol)

(defprotocol MaksutServiceProtocol
  (create [this session lasku])
  (create-tutu [this session lasku])
  (list-tutu [this session input])
  (list [this session input])
  (check-status-tutu [this session input])
  (check-status [this session input])
  (get-lasku [this session order-id])
  (get-laskut-by-secret [this session secret]))
