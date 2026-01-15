(ns maksut.email.it-email-service
  (:require [com.stuartsierra.component :as component]
            [maksut.email.email-service :as email-service]
            [maksut.email.email-service-protocol :refer [EmailServiceProtocol]]
            [ring.adapter.jetty :as jetty]))


(defn- tgt [_]
  {:status 201
   :headers {"location" "http://localhost:8090/cas/v1/tickets/TGT-123"}})

(defn- st [_]
  {:status 200
   :body "ST-1234"})

(defn- session [_]
  {:status 200
   :headers {"set-cookie" "JSESSIONID=foobar"}
   :body ""})

(defn- lahetys-mock [_]
  {:status  200
   :headers {}
   :body    "{\"lahetysTunniste\":\"0181a38f-0883-7a0e-8155-83f5d9a3c226\"}"})

(defn- viestit-mock [_]
  {:status  200
   :headers {}
   :body    "{\"viestiTunniste\":\"3fa85f64-5717-4562-b3fc-2c963f66afa6\",\"lahetysTunniste\":\"0181a38f-0883-7a0e-8155-83f5d9a3c226\"}"})

(def routes
  {"/cas/v1/tickets"                            tgt
   "/cas/v1/tickets/TGT-123"                    st
   "/lahetys/login/j_spring_cas_security_check" session
   "/lahetys/v1/lahetykset"                     lahetys-mock
   "/lahetys/v1/viestit"                        viestit-mock})

(defmacro with-mock-server [& body]
  `(let [handlers# (fn [request#]
                     (if-let [handler# (routes (:uri request#))]
                       (handler# request#)
                       {:status 404
                        :body   "Not Found"}))
         jetty# (jetty/run-jetty handlers# {:port 8090 :daemon? true :join? false})]
     (try ~@body
          (finally (.stop jetty#)))))

; Integraatio-testien ajamiseen.
; Käynnistää mockup-serverin viestinvälitys-palvelulle ja CASille.
; Lähettää sähköpostit mockupille käyttäen viestinvälitys-kirjastoa, tarkistaen lähinnä, että kirjasto ei heitä poikkeuksia.
; Lisäksi sähköpostit kirjoitetaan mock-email-service-list -atomiin, josta testit voivat ne lukea.
(defrecord ItEmailService [config mock-email-service-list]
  component/Lifecycle
  (start [this]
    (assoc this :viestinvalitys-client (email-service/create-viestinvalitys-client config)))
  (stop [this]
    (assoc this :mock-jetty nil :viestinvalitys-client nil))
  EmailServiceProtocol
  (send-email [this viesti]
    (with-mock-server (email-service/send-email this viesti))
    (let [^String from (-> viesti .getLahettaja .get .getSahkopostiOsoite .get)
          recipients (->> viesti .getVastaanottajat .get (map #(-> % .getSahkopostiOsoite .get)))
          ^String subject (-> viesti .getOtsikko .get)
          ^String body (-> viesti .getSisalto .get)]
      (reset! mock-email-service-list
              (conj @mock-email-service-list
                    {:from       from
                     :recipients recipients
                     :subject    subject
                     :body       body})))))
