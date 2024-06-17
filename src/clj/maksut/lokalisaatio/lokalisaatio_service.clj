(ns maksut.lokalisaatio.lokalisaatio-service
  (:require [maksut.oph-url-properties :as url]
            [maksut.lokalisaatio.lokalisaatio-service-protocol :as lokalisaatio-protocol]
            [clj-http.client :as http]))

(defrecord LokalisaatioService
  [config]

  lokalisaatio-protocol/LokalisaatioServiceProtocol
  (get-localisations [_ lang]
    (let [url (url/resolve-url :lokalisointi-service.get-lokalisations config lang)
          response (http/get url {:as :json
                                  :headers {"Caller-Id" "1.2.246.562.10.00000000001.maksut.backend"}})]
      (reduce
        #(assoc %1 (keyword (:key %2)) (:value %2))
        {}
        (:body response)))))