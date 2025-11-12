(ns maksut.cas.cas-ticket-client
  (:require [clojure.data.xml :as data-xml]
            [com.stuartsierra.component :as component]
            [maksut.cas.cas-ticket-client-protocol :as cas-ticket-client-protocol]
            [maksut.http :as http]
            [maksut.oph-url-properties :as url]
            [taoensso.timbre :as log])
  (:import (clojure.data.xml Element)))

(def maksut-crud-permission "ROLE_APP_MAKSUT_CRUD")

(defn xml->map
  "Convert XML data to Clojure map"
  [x]
  (hash-map
    (:tag x)
    (map
      #(if (instance? Element %)
         (xml->map %)
         %)
      (:content x))))

(defn- find-value
  "Recursively fetch the given keypath in map (similar to get-in), but for
  lists, fetch the first element that has the key we're looking for next"
  [data keypath]
  (reduce (fn [m key] (or (get m key) (some #(get % key) m)))
          data keypath))

(defn- convert-response-data
  "Extracts user and/or error info from response data"
  [data]
  (let [m        (xml->map data)
        response (find-value m [:serviceResponse :authenticationSuccess])
        roles    (keep (comp first :roles) (find-value response [:attributes]))]
    {:success?       (some? response)
     :authorized?    (contains? (set roles) maksut-crud-permission)
     :error          (first (find-value m [:serviceResponse :authenticationFailure]))
     :username       (first (find-value response [:user]))
     :kayttajaTyyppi (first (find-value response
                                        [:attributes :kayttajaTyyppi]))
     :oidHenkilo     (first (find-value response [:attributes :oidHenkilo]))
     :roles          roles}))

(defn parse-username [^String xml-response]
  (try
    (let [validation-data (-> xml-response (data-xml/parse-str) (convert-response-data))]
      (if (:success? validation-data)
        (if (:authorized? validation-data)
          (:username validation-data)
          (log/warn "Käyttäjältä puuttuu vaadittava oikeus (" maksut-crud-permission ")" validation-data))
        (log/warn "Service ticketin validointi epäonnistui" validation-data)))
    (catch Exception e
      (log/error e (str "Ongelma käsiteltäessä CASin ticket-validoinnin vastausta '" xml-response "'"))
      (throw e))))

(defn- assert-ok-response [response]
  (when (not= 200 (:status response))
    (throw (RuntimeException. (str "Saatiin ei-OK-vastaus CASilta: " response))))
  response)

(defrecord CasTicketClient [config]
  component/Lifecycle
  (start [this]
    ;TODO use different variable than :maksut.login-success
    (let [service-parameter (url/resolve-url :maksut.login-success config)]
      (log/info "Staring CasTicketClient with service-parameter: " service-parameter)
      (assoc this :service-parameter service-parameter)))

  (stop [this]
    (assoc this :service-parameter nil))

  cas-ticket-client-protocol/CasTicketClientProtocol
  (validate-service-ticket [this ticket]
    (-> (http/do-request {:method :get
                          :url    (url/resolve-url :cas.validate-service-ticket config {:ticket  ticket
                                                                                        :service (:service-parameter this)})
                          :body   {}}
                         {:request-schema  {}
                          :response-schema {}}
                         config)
        (assert-ok-response)
        (:body)
        (parse-username))))

(defrecord FakeCasTicketClient []
  cas-ticket-client-protocol/CasTicketClientProtocol
  (validate-service-ticket [_ ticket]
    (if (= ticket "USER-WITH-HAKUKOHDE-ORGANIZATION")
      "1.2.246.562.11.22222222222"
      "1.2.246.562.11.11111111111")))
