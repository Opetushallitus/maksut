(ns maksut.lokalisaatio.lokalisaatio-service
  (:require [clojure.string :as str]
            [maksut.oph-url-properties :as url]
            [maksut.lokalisaatio.lokalisaatio-service-protocol :as lokalisaatio-protocol]
            [clj-http.client :as http]
            [maksut.config :refer [production-environment?]]))

(def dev-translations
  {:invoice.notfound                   {:fi "Laskua ei löydy"
                                        :en "Invoice cannot be found."
                                        :sv "Fakturan hittades inte"}
   :invoice.notfound.secret            {:fi "Maksulinkki on väärä."
                                        :en "Link to the payment is invalid."
                                        :sv "Fel länk till betalning."}
   :invoice.notfound.oldsecret         {:fi "Maksulinkki on vanhentunut."
                                        :en "Link to the payment is too old."
                                        :sv "Länk till betalning är för gammal."}
   :invoice.invalidstate.overdue       {:fi "Lasku on erääntynyt"
                                        :en "The invoice has expired."
                                        :sv "Fakturan har förfallit"}
   :invoice.invalidstate.paid          {:fi "Lasku on jo maksettu"
                                        :en "The invoice has already been paid."
                                        :sv "Fakturan har redan betalats"}
   :Maksu.keskeytetty                  {:fi "Maksu keskeytetty"
                                        :en "Payment has been terminated."
                                        :sv "Betalning avbruten."}
   :http.virhe                         {:fi "Tietojen haku epäonnistui"
                                        :en "Information search failed."
                                        :sv "Sökning av uppgifterna misslyckades."}
   :Header.title                       {:fi "Maksutapahtumat"
                                        :en "Payments"
                                        :sv "Betalningar"}
   :TutuPanel.title                    {:fi "Tutkinnon tunnustaminen"
                                        :en "Recognition of qualifications"
                                        :sv "Erkännande av examina"}
   :Maksu.käsittely                    {:fi "Käsittelymaksu"
                                        :en "Processing fee"
                                        :sv "Behandlingsavgift"}
   :Maksu.päätös                       {:fi "Päätösmaksu"
                                        :en "Decision fee"
                                        :sv "Beslutsavgift"}
   :Maksu.maksu                        {:fi "Maksu"
                                        :en "Fee"
                                        :sv "Avgift"}
   :MaksutPanel.maksa                  {:fi "Siirry maksamaan"
                                        :en "Go to payment"
                                        :sv "Gå till betalning"}
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
                                        :sv "Betalningsdag"}
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
   :AstuPanel.päätösMaksettu           {:fi "Olet maksanut maksun. Lähetämme kuitin maksusta sähköpostiisi."
                                        :en "EN Olet maksanut maksun. Lähetämme kuitin maksusta sähköpostiisi."
                                        :sv "SV Olet maksanut maksun. Lähetämme kuitin maksusta sähköpostiisi."}
   :AstuPanel.päätösMaksamatta         {:fi "Hakemuksesi on käsitelty. Siirry maksamaan maksu."
                                        :en "EN Hakemuksesi on käsitelty. Siirry maksamaan maksu."
                                        :sv "SV Hakemuksesi on käsitelty. Siirry maksamaan maksu."}
   :MaksutPanel.yhteiskäytto           {:fi "Jos käytät yhteiskäyttöistä tietokonetta, muista tyhjentää selaimen välimuisti ja kirjautua ulos koneelta. Voit nyt sulkea tämän ikkunan."
                                        :en "If you are using a computer that is in common use, remember to empty the cache and log out from the computer. You can now close this window."
                                        :sv "Om du använder en dator som är i gemensamt bruk, kom ihåg att tomma cacheminnen och logga ut ur datorn. Du kan nu stänga det här fönstret."}

   ;:invoice-not-found {:paid-oldsecret-header     {:fi "Linkki laskuun on vanhentunut"
   ;                                                :en "Link to the payment is too old"
   ;                                                :sv "Länk till betalning är för gammal"}
   ;                    :processing-overdue-header {:fi "Hakemuksesi käsittelymaksun eräpäivä on mennyt umpeen"
   ;                                                :en "The due date for the processing fee of your application has passed"
   ;                                                :sv "Förfallodagen för behandlingsavgiften för din ansökan har gått ut"}
   ;                    :processing-overdue-text-1 {:fi "Et ole suorittanut 70 euron käsittelymaksua eräpäivään mennessä. Hakemuksesi on rauennut. Jos haluat edelleen hakea tutkintosi tunnustamista, täytä hakulomake uudelleen."
   ;                                                :en "You have not paid the processing fee of EUR 70 by the due date. Your application has expired. If you still wish to apply for recognition of your qualification, please fill out the application form again."
   ;                                                :sv "Du har inte betalt behandlingsavgiften på 70 euro före förfallodagen. Din ansökan har förfallit. Om du vill ännu ansöka om erkännande av din examen, lämna in en ny ansökan."}
   ;                    :processing-overdue-text-2 {:fi "Jos sinulla on kysyttävää, lähetä sähköpostia osoitteeseen "
   ;                                                :en "If you have any questions, please contact us by email at "
   ;                                                :sv "Om du har frågor, kontakta oss per e-post "}
   ;                    :processing-overdue-text-3 {:fi "Linkki tutkintojen tunnustamisen etusivulle: "
   ;                                                :en "Link to the homepage of recognition of qualifications: "
   ;                                                :sv "Länk till webbsidor för erkännande av examina: "}
   ;                    :decision-overdue-header   {:fi "Hakemuksesi päätösmaksun eräpäivä on mennyt umpeen"
   ;                                                :en "The due date for the decision fee of your application has passed"
   ;                                                :sv "Förfallodagen för beslutsavgiften för din ansökan har gått ut."}
   ;                    :decision-overdue-text-1   {:fi "Et ole suorittanut päätösmaksua eräpäivään mennessä. Lähetämme sinulle päätösmaksusta erillisen laskun. Hakemasi päätös lähetetään sinulle vasta päätösmaksun suorittamisen jälkeen."
   ;                                                :en "You have not paid the decision fee by the due date. We will send you a separate invoice for the decision fee. We will not send the decision until after you have paid the decision fee."
   ;                                                :sv "Du har inte betalt beslutsavgiften före förfallodagen. Vi skickar dig en separat faktura för beslutsavgiften. Vi skickar dig beslutet först då du har betalat beslutsavgiften."}
   ;                    :decision-overdue-text-2   {:fi "Maksu perustuu opetus- ja kulttuuriministeriön voimassa olevaan asetukseen Opetushallituksen suoritteiden maksullisuudesta. Maksu on ulosottokelpoinen ilman tuomiota tai päätöstä (valtion maksuperustelaki (150/1992) 11§ 1.mom.)."
   ;                                                :en "The fee is based on the Ministry of Education and Culture’s Decree on the Fees on the Services Provided by the Finnish National Agency for Education. The fee is enforceable without a judgement or a decision (Act on Criteria for Charges Payable to the State 150/1992, section 11, subsection 1)."
   ;                                                :sv "Avgiften är baserad på undervisnings- och kulturministeriets gällande förordning om Utbildningsstyrelsens avgiftsbelagda prestationer. Avgiften från indrivas utan dom eller beslut (lag om grunderna för avgifter till staten (150/1992 11 § 1 mom.)."}
   ;                    :decision-overdue-text-3   {:fi "Laskuun liittyvissä kysymyksissä voit olla suoraan yhteydessä Opetushallituksen myyntilaskutukseen osoitteessa "
   ;                                                :en "With questions regarding the invoices, please contact our invoicing services directly at "
   ;                                                :sv "Om du har frågor om fakturan, vänligen kontakta Utbildningsstyrelsens fakturering per e-post "}
   ;                    :decision-overdue-text-4   {:fi "Lisätietoja saat sähköpostitse osoitteesta "
   ;                                                :en "For more information, please contact us by email at "
   ;                                                :sv "Mer information får du per e-post "}
   ;                    :invalid-secret-header     {:fi "Maksun tietoja ei löydy"
   ;                                                :en "The details of your payment cannot be found"
   ;                                                :sv "Uppgifterna om avgiften hittades inte"}
   ;                    :invalid-secret-text       {:fi "Maksusi tila ei ole nähtävissä. Tilanteen selvittämiseksi ota yhteyttä Opetushallituksen Tutkintojen ja kieliosaamisen tunnustaminen -yksikköön sähköpostitse osoitteessa "
   ;                                                :en "The status of your payment is not available. To clear up the situation, please contact the Finnish National Agency for Education’s unit for recognition of qualifications and language proficiency, please contact us by email at "
   ;                                                :sv "Status för avgiften är inte tillgänglig. För att reda ut situationen, kontakta Utbildningsstyrelsens enhet för erkännande av examina och språkkunskaper per e-post "}}
   })

; Supports only 2 level hierarchy, e.g. "Maksu.status" not "Maksu.status.active"
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