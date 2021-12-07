(ns maksut.cas.cas-ticket-client
  (:require [clojure.xml :as xml]
            [com.stuartsierra.component :as component]
            [maksut.cas.cas-ticket-client-protocol :as cas-ticket-client-protocol]
            [maksut.http :as http]
            [maksut.oph-url-properties :as url]
            [taoensso.timbre :as log])
  (:import [java.io ByteArrayInputStream]
           java.util.UUID))

(defn- check-result [parse-result]
  (if (and (some? parse-result) (> (.length parse-result) 0))
    parse-result
    (throw (IllegalArgumentException. (str "Ei pystytty jäsentämään käyttäjätunnusta CASin vastauksesta")))))

(defn- parse-username [^String xml-response]
  (try
    (-> xml-response
        (.getBytes)
        (ByteArrayInputStream.)
        (xml/parse)
        (:content)
        (first)
        (:content)
        (first)
        (:content)
        (first)
        (check-result))
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
    [(-> (http/do-request {:method :get
                           :url    (url/resolve-url :cas.validate-service-ticket config {:ticket  ticket
                                                                                         :service (:service-parameter this)})
                           :body   {}}
                          {:request-schema  {}
                           :response-schema {}}
                          config)
         (assert-ok-response)
         (:body)
         (parse-username))
     ticket]))

(defrecord FakeCasTicketClient []
  cas-ticket-client-protocol/CasTicketClientProtocol
  (validate-service-ticket [_ ticket]
    [(if (= ticket "USER-WITH-HAKUKOHDE-ORGANIZATION")
       "1.2.246.562.11.22222222222"
       "1.2.246.562.11.11111111111")
     (str (UUID/randomUUID))]))
