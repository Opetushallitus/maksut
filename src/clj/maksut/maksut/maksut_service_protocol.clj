(ns maksut.maksut.maksut-service-protocol)

(defprotocol MaksutServiceProtocol
  (create [this session lasku])
  (create-tutu [this session lasku])
  (list-laskut [this session input])
  (check-status [this session input])
  (get-lasku [this session order-id])
  (get-lasku-contact [this session secret])
  (get-laskut-by-secret [this session secret]))
