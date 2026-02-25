(ns maksut.email.email-message-handling-spec
  (:require [clojure.test :refer [deftest is testing]]
            [maksut.email.email-message-handling :refer [->vastaanottajat ->viesti]])
  (:import (fi.oph.viestinvalitys.vastaanotto.model LahetysImpl)
           (java.util List Optional)))

(deftest vastaanottajat-test
  (testing "Empty recipients"
    (let [result ^List (->vastaanottajat [])]
      (is (empty? result))))
  (testing "One recipient"
    (let [result ^List (->vastaanottajat ["test@example.test"])]
      (is (= (.size result) 1))
      (is (= (.getNimi (first result)) (Optional/empty)))
      (is (= (.getSahkopostiOsoite (first result)) (Optional/of "test@example.test")))
      ))
  (testing "Several recipients"
    (let [result ^List (->vastaanottajat ["test@example.test" "toinen@example.test" "kolmas@example.test"])]
      (is (= (.size result) 3))
      (is (every? #(= (.getNimi %) (Optional/empty)) result))
      (is (= (map #(-> (.getSahkopostiOsoite %) (.get)) result) ["test@example.test" "toinen@example.test" "kolmas@example.test"])))))

(deftest viesti-test
  (let [email-data {:from       "lahettaja"
                    :recipients ["first-sender" "second-sender"]
                    :subject    "Subject line"
                    :lang       "en"}
        result (->viesti email-data "Body")]
    (testing "Uses the data from email-data"
      (is (= (-> result .getLahettaja .get .getSahkopostiOsoite .get) (:from email-data)))
      (is (= (-> result .getLahettaja .get .getNimi) (Optional/of "Opetushallitus")))
      (is (= (->> result .getVastaanottajat .get (map #(-> % .getSahkopostiOsoite .get))) (:recipients email-data)))
      (is (= (->> result .getVastaanottajat .get (map #(-> % .getNimi))) [(Optional/empty) (Optional/empty)]))
      (is (= (-> result .getOtsikko .get) (:subject email-data)))
      (is (= (-> result .getKielet .get) ["en"])))
    (testing "Sets the correct access permissions"
      (let [kayttooikeudet (->> result .getKayttooikeusRajoitukset .get)]
        (is (= (map #(-> % .getOikeus .get) kayttooikeudet) ["APP_VIESTINVALITYS_OPH_PAAKAYTTAJA"]))
        (is (= (map #(-> % .getOrganisaatio .get) kayttooikeudet) ["1.2.246.562.10.00000000001"]))))
    (testing "Sets the correct metadata"
      (is (= (-> result .getLahettavaPalvelu .get) "maksut"))
      (is (= (-> result .getPrioriteetti .get) (LahetysImpl/LAHETYS_PRIORITEETTI_NORMAALI)))
      (is (= (-> result .getSailytysaika .get) 2000)))))
