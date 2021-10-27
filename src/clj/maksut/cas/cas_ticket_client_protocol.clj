(ns maksut.cas.cas-ticket-client-protocol)

(defprotocol CasTicketClientProtocol
  (validate-service-ticket [this ticket]))
