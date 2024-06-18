(ns maksut.lokalisaatio.lokalisaatio-service-protocol)

(defprotocol LokalisaatioServiceProtocol
  (get-localisations [this lang]))