(ns maksut.lokalisaatio.lokalisaatio-service
  (:require [clojure.string :as str]
            [maksut.oph-url-properties :as url]
            [maksut.lokalisaatio.lokalisaatio-service-protocol :as lokalisaatio-protocol]
            [clj-http.client :as http]
            [maksut.config :refer [production-environment?]]
            [maksut.lokalisaatio.translations :refer [maksut-ui-local-translations]]
            [taoensso.timbre :as log]))

; Supports only 2 level hierarchy, e.g. "Maksu.active" not "Maksu.status.active"
(defn- parse-messages [messages]
  (reduce-kv
    (fn [acc ns-key val]
      (let [[namespace key] (str/split (name ns-key) #"\.")
            ns (keyword namespace)
            k (keyword key)]
        (assoc acc ns (merge (ns acc) {k val}))))
    {}
    messages))

(defn- http-get [url timeout-ms]
   (let [response (http/get url {:as :json
                                 :headers {"Caller-Id" "1.2.246.562.10.00000000001.maksut.backend"}
                                 :socket-timeout timeout-ms
                                 :connection-timeout timeout-ms})]
     (:body response)))

(defn- get-with-retries [url timeout remaining-retries]
  (try
       (http-get url timeout)
       (catch Exception e
         (if (> remaining-retries 0)
           (do
             (log/warn e "Retrying calling lokalisaatio with url:" url "")
             (Thread/sleep 1000)
             (get-with-retries url timeout (- remaining-retries 1)))
           (do
             (log/warn e "Giving up on calling lokalisaatio with url:" url)
             (throw e))))))

(defrecord LokalisaatioService
  [config]

  lokalisaatio-protocol/LokalisaatioServiceProtocol
  (get-localisations [_ lang]
    (if (production-environment? config)
      (let [url (url/resolve-url :lokalisointi-service.get-lokalisations config lang)
            response (http/get url {:as :json
                                    :headers {"Caller-Id" "1.2.246.562.10.00000000001.maksut.backend"}})]
        (parse-messages
          (reduce
            #(assoc %1 (keyword (:key %2)) (:value %2))
            {}
            (:body response))))
      (parse-messages
        (reduce-kv
          #(assoc %1 %2 (get %3 (keyword lang)))
          {}
          maksut-ui-local-translations))))

  (get-localisation [_ lang key]
    (if (production-environment? config)
      (let [url (url/resolve-url :lokalisointi-service.get-localisation-by-key config lang key)
            body (get-with-retries url 5000 2)]
        (-> body first :value))
      ((maksut-ui-local-translations (keyword key)) (keyword lang)))))
