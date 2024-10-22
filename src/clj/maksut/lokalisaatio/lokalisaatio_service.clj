(ns maksut.lokalisaatio.lokalisaatio-service
  (:require [clojure.string :as str]
            [maksut.oph-url-properties :as url]
            [maksut.lokalisaatio.lokalisaatio-service-protocol :as lokalisaatio-protocol]
            [clj-http.client :as http]
            [maksut.config :refer [production-environment?]]))

(def dev-translations
  {:Header.title                       {:fi "Maksutapahtumat"
                                        :en "Payments"
                                        :sv "Betalningar"}
   :TutuPanel.title                    {:fi "Tutkinnon tunnustaminen"
                                        :en "Recognition of qualifications"
                                        :sv "Erkännande av examina"}
   :TutuPanel.käsittely                {:fi "Hakemuksen käsittely"
                                        :en "Processing of the application"
                                        :sv "Behandling av ansökan"}
   :TutuPanel.päätös                   {:fi "Päätösmaksu"
                                        :en "Decision fee"
                                        :sv "Beslutsavgift"}
   :TutuPanel.käsittelyMaksamatta1     {:fi "Hakemuksesi on tallentunut. Hakemuksesi otetaan käsittelyyn, kun olet maksanut käsittelymaksun."
                                        :en "Your application has been saved. Your application will be taken into processing when you have paid the processing fee."
                                        :sv "Din ansökan har sparats. Din ansökan tas till behandling när du har betalat behandlingsavgiften."}
   :TutuPanel.käsittelyMaksamatta2     {:fi "Jos olet täyttänyt tutkintojen tunnustamisen hakulomakkeen, sinun pitää myöhemmin maksaa erillinen päätösmaksu ennen kuin saat päätöksen. Kun olemme tehneet päätöksen hakemuksestasi, lähetämme sinulle päätösmaksun maksuohjeet sähköpostitse."
                                        :en "If you have filled out the application for recognition of qualifications, you will later have to pay a separate decision fee before you can receive the decision. We will send you the instructions for the payment of the decision fee by email when we have made the decision on your application."
                                        :sv "Om du har fyllt i ansökan om erkännande av examina, ska du betala en separat beslutsavgift innan du får beslutet. När vi har fattat beslut om din ansökan, skickar vi dig betalningsanvisningarna för beslutsavgiften per e-post."}
   :TutuPanel.käsittelyMaksamatta3     {:fi "Jos olet täyttänyt lopullisen päätöksen hakulomakkeen, sinulta ei peritä erillistä päätösmaksua."
                                        :en "If you have filled out the application for a final decision, a separate decision fee will not be charged."
                                        :sv "Om du har fyllt i ansökan om ett slutligt beslut, tas en separat beslutsavgift inte ut."}
   :TutuPanel.käsittelyMaksamatta4     {:fi "Emme palauta käsittelymaksua, vaikka peruuttaisit hakemuksesi."
                                        :en "We will not return the processing fee to you even if you cancel your application."
                                        :sv "Vi returnerar inte den betalda behandlingsavgiften även om du skulle dra tillbaka din ansökan."}
   :TutuPanel.käsittelyMaksettu1       {:fi "Olet maksanut käsittelymaksun. Olet saanut sähköpostiisi vahvistuksen maksusta. Olet saanut myös kuitin maksustasi erillisellä sähköpostilla. Hakemuksesi siirtyy seuraavaksi käsittelyyn."
                                        :en "You have paid the processing fee. A confirmation of the transaction has been sent to your email. You have also received a receipt for your payment in a separate email. Your application will now be taken into processing."
                                        :sv "Du har betalat behandlingsavgiften. Du har fått en bekräftelse om betalningen till din e-post. Kvittot på betalningen har du fått som ett separat epost. Din ansökan tas nu till behandling."}
   :TutuPanel.käsittelyMaksettu2       {:fi "Jos olet täyttänyt tutkintojen tunnustamisen hakulomakkeen, sinun pitää myöhemmin maksaa erillinen päätösmaksu. Kun olemme tehneet päätöksen hakemuksestasi, lähetämme sinulle sähköpostitse maksuohjeet päätösmaksusta. Lähetämme päätöksen vasta, kun olet maksanut päätösmaksun."
                                        :en "If you have filled out an application for recognition of qualifications, you will later have to pay a separate decision fee before you can receive the decision. We will send you the instructions for the payment of the decision fee by email when we have made the decision on your application. We will send you the decision when you have paid the decision fee."
                                        :sv "Om du har fyllt i ansökan om erkännande av examina, ska du betala en separat beslutsavgift innan du får beslutet. När vi har fattat beslut om din ansökan, skickar vi dig betalningsanvisningarna för beslutsavgiften per e-post. Vi skickar beslutet först när du har betalat beslutsavgiften."}
   :TutuPanel.käsittelyMaksettu3       {:fi "Jos olet täyttänyt lopullisen päätöksen hakulomakkeen, sinulta ei peritä erillistä päätösmaksua."
                                        :en "If you have filled out an application for a final decision, a separate decision fee will not be charged."
                                        :sv "Om du har fyllt i ansökan om ett slutligt beslut, tas en separat beslutsavgift inte ut."}
   :TutuPanel.päätösMaksamatta         {:fi "Hakemuksesi on käsitelty ja päätös tutkintosi tunnustamisesta on nyt tehty. Siirry maksamaan päätösmaksu. Lähetämme päätöksen sinulle, kun olet maksanut päätösmaksun."
                                        :en "Your application has been processed and a decision on recognition of your qualification has been made. Proceed to the payment of the decision fee. We will send you the decision once you have paid the decision fee."
                                        :sv "Din ansökan har nu behandlats och beslutet om erkännande av din examen fattats. Gå till betalning av beslutsavgiften. Vi skickar beslutet till dig när du har betalat beslutsavgiften."}
   :TutuPanel.päätösMaksettu           {:fi "Olet maksanut päätösmaksun. Olet saanut vahvistuksen maksun onnistumisesta sähköpostiisi. Olet saanut myös kuitin maksustasi erillisellä sähköpostilla. Lähetämme päätöksen sinulle mahdollisimman pian. Otathan huomioon, että tämä voi viedä jopa kolme työpäivää."
                                        :en "You have paid the decision fee. A confirmation of the transaction has been sent to your email. You have also received a receipt for your payment in a separate email. We will send the decision to you as soon as possible. Please note that this might take up to three working days."
                                        :sv "Du har betalat beslutsavgiften. Du har fått en bekräftelse om betalningen till din e-post. Kvittot på betalningen har du fått som ett separat epost. Vi skickar beslutet till dig så snart som möjligt. Vänligen notera att detta kan ta upp till tre arbetsdagar."}
   :TutuStateTracker.käsittely         {:fi "Hakemuksen käsittely"
                                        :en "Processing of the application"
                                        :sv "Behandling av ansökan"}
   :TutuStateTracker.käsittelymaksu    {:fi "Käsittelymaksu"
                                        :en "Processing fee"
                                        :sv "Behandlingsavgift"}
   :TutuStateTracker.päätösmaksu       {:fi "Päätösmaksu"
                                        :en "Decision fee"
                                        :sv "Beslutsavgift"}
   :AstuPanel.päätösMaksettu           {:fi "Olet maksanut maksun. Lähetämme kuitin maksusta sähköpostiisi."
                                        :en "EN Olet maksanut maksun. Lähetämme kuitin maksusta sähköpostiisi."
                                        :sv "SV Olet maksanut maksun. Lähetämme kuitin maksusta sähköpostiisi."}
   :AstuPanel.päätösMaksamatta         {:fi "Hakemuksesi on käsitelty. Siirry maksamaan maksu."
                                        :en "EN Hakemuksesi on käsitelty. Siirry maksamaan maksu."
                                        :sv "SV Hakemuksesi on käsitelty. Siirry maksamaan maksu."}
   :MaksutPanel.yhteiskäytto           {:fi "Jos käytät yhteiskäyttöistä tietokonetta, muista tyhjentää selaimen välimuisti ja kirjautua ulos koneelta. Voit nyt sulkea tämän ikkunan."
                                        :en "If you are using a computer that is in common use, remember to empty the cache and log out from the computer. You can now close this window."
                                        :sv "Om du använder en dator som är i gemensamt bruk, kom ihåg att tomma cacheminnen och logga ut ur datorn. Du kan nu stänga det här fönstret."}
   :MaksutPanel.maksa                  {:fi "Siirry maksamaan"
                                        :en "Go to payment"
                                        :sv "Gå till betalning"}
   :Maksu.keskeytetty                  {:fi "Maksu keskeytetty"
                                        :en "Payment has been terminated."
                                        :sv "Betalning avbruten."}
   :Maksu.käsittely                    {:fi "Käsittelymaksu"
                                        :en "Processing fee"
                                        :sv "Behandlingsavgift"}
   :Maksu.päätös                       {:fi "Päätösmaksu"
                                        :en "Decision fee"
                                        :sv "Beslutsavgift"}
   :Maksu.maksu                        {:fi "Maksu"
                                        :en "Fee"
                                        :sv "Avgift"}
   :Maksu.tila                         {:fi "Tila"
                                        :en "Status"
                                        :sv "Status"}
   :Maksu.active                       {:fi "Maksamatta"
                                        :en "Unpaid"
                                        :sv "Obetald"}
   :Maksu.paid                         {:fi "Maksettu"
                                        :en "Paid"
                                        :sv "Betald"}
   :Maksu.overdue                      {:fi "Erääntynyt"
                                        :en "Expired"
                                        :sv "Förfallen"}
   :Maksu.summa                        {:fi "Määrä"
                                        :en "Amount"
                                        :sv "Summa"}
   :Maksu.eräpäivä                     {:fi "Eräpäivä"
                                        :en "Due date"
                                        :sv "Förfallodagen"}
   :Maksu.maksupäivä                   {:fi "Maksupäivä"
                                        :en "Payment date"
                                        :sv "Betalningsdag"}})

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
          dev-translations)))
    ))