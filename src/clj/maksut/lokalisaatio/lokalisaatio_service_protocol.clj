(ns maksut.lokalisaatio.lokalisaatio-service-protocol)

(defprotocol LokalisaatioServiceProtocol
  (get-localisations [this lang])
  (get-localisation [this lang key]))
