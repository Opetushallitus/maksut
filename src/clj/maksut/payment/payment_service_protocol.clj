(ns maksut.payment.payment-service-protocol)

(defprotocol PaymentServiceProtocol
  (payment [this session params])
  ;Both success and notify requests are processed here, this function is idempotent.
  ;However if payment-id differs from previous calls, new payment will be stored.
  (process-success-callback [this params locale notify?])
  (get-kuitti [this session params]))

