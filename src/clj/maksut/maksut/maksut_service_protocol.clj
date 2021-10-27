(ns maksut.maksut.maksut-service-protocol)

(defprotocol MaksutServiceProtocol
  (create [this session lasku])
  (create-tutu [this session lasku])
  (get-lasku [this session order-id])
  (get-laskut-by-secret [this session secret]))
