(ns maksut.payment.payment-service-protocol)

(defprotocol PaymentServiceProtocol
  (tutu-payment [this params])
  ;Both success and notify requests are processed here, this function is "idempotent" - however it payment-id
  ;differs from previous calls, new payment will be stored
  (process-success-callback [this params notify?])
  (form-data-for-payment [this params])
  (authentic-response? [this form-data]))

