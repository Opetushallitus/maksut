(ns maksut.cas.cas-ticket-client-test
  (:require [clojure.test :refer [deftest is testing]]
            [maksut.cas.cas-ticket-client :as client]
            [maksut.oph-url-properties :as url]
            [maksut.cas.cas-ticket-client-protocol :as cas-ticket-client-protocol]
            [maksut.http :as http]))

(def authorized-xml-response "
<cas:serviceResponse xmlns:cas='http://www.yale.edu/tp/cas'>
    <cas:authenticationSuccess>
        <cas:user>virkahemmo</cas:user>
        <cas:attributes>
            <cas:oidHenkilo>1.2.246.562.24.12345678901</cas:oidHenkilo>
            <cas:kayttajaTyyppi>VIRKAILIJA</cas:kayttajaTyyppi>
            <cas:idpEntityId>usernamePassword</cas:idpEntityId>
            <cas:roles>ROLE_APP_ATARU_EDITORI_CRUD</cas:roles>
            <cas:roles>ROLE_APP_MAKSUT_CRUD</cas:roles>
            <cas:roles>ROLE_APP_ATARU_EDITORI_CRUD_1.2.246.562.10.00000000001</cas:roles>
        </cas:attributes>
    </cas:authenticationSuccess>
</cas:serviceResponse>")

(def unauthorized-xml-response "
<cas:serviceResponse xmlns:cas='http://www.yale.edu/tp/cas'>
  <cas:authenticationSuccess>
    <cas:user>virkahemmo</cas:user>
    <cas:attributes>
      <cas:oidHenkilo>1.2.246.562.24.12345678901</cas:oidHenkilo>
      <cas:kayttajaTyyppi>VIRKAILIJA</cas:kayttajaTyyppi>
      <cas:idpEntityId>usernamePassword</cas:idpEntityId>
      <cas:roles>ROLE_APP_ATARU_EDITORI</cas:roles>
      <cas:roles>ROLE_APP_ATARU_EDITORI_CRUD</cas:roles>
      <cas:roles>ROLE_APP_ATARU_EDITORI_CRUD_1.2.246.562.10.00000000001</cas:roles>
    </cas:attributes>
  </cas:authenticationSuccess>
</cas:serviceResponse>")

(def failed-xml-response "
<cas:serviceResponse xmlns:cas='http://www.yale.edu/tp/cas'>
  <cas:authenticationFailure code=\"INVALID_REQUEST\">No ticket string provided</cas:authenticationFailure>
</cas:serviceResponse>")

(deftest parse-username-test
  (testing "Palauttaa käyttäjänimen, kun CAS-vastaus sisältää oikeat oikeudet"
    (is (= nil (client/parse-username failed-xml-response)))
    (is (= nil (client/parse-username unauthorized-xml-response)))
    (is (= "virkahemmo" (client/parse-username authorized-xml-response)))))

(deftest validate-service-ticket-test
  (testing "Palauttaa käyttäjänimen, kun CAS-vastaus on kelvollinen"
    (with-redefs [http/do-request (fn [_ _ _] {:status 200 :body authorized-xml-response})
                  url/resolve-url (fn [_ _ _] "url")]
      (let [impl (client/map->CasTicketClient {})]
        (is (= "virkahemmo" (cas-ticket-client-protocol/validate-service-ticket impl "ticket"))))))
  (testing "Heittää poikkeuksen, kun CAS-vastaus on virheellinen"
    (with-redefs [http/do-request (fn [_ _ _] {:status 500})
                  url/resolve-url (fn [_ _ _] "url")]
      (let [impl (client/map->CasTicketClient {})]
        (is (thrown-with-msg? RuntimeException #"Saatiin ei-OK-vastaus CASilta: \{:status 500\}" (cas-ticket-client-protocol/validate-service-ticket impl "ticket")))))))
