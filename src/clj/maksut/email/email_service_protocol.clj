(ns maksut.email.email-service-protocol)

(defprotocol EmailServiceProtocol
  (send-email [this from recipients subject body]))
