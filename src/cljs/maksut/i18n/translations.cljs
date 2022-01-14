(ns maksut.i18n.translations)

(def local-translations
  {:rest-error     {:invoice-notfound                   {:fi "Laskua ei löydy"
                                                         :en "Invoice cannot be found."
                                                         :sv "Fakturan hittades inte"}
                    :invoice-notfound-secret            {:fi "Maksulinkki on väärä."
                                                         :en "Link to the payment is invalid."
                                                         :sv "Fel länk till betalning."}
                    :invoice-notfound-oldsecret         {:fi "Maksulinkki on vanhentunut."
                                                         :en "Link to the payment is too old."
                                                         :sv "Länk till betalning är för gammal."}
                    :invoice-invalidstate-overdue       {:fi "Lasku on erääntynyt"
                                                         :en "The invoice has expired."
                                                         :sv "Fakturan har förfallit"}
                    :invoice-invalidstate-paid          {:fi "Lasku on jo maksettu"
                                                         :en "The invoice has already been paid."
                                                         :sv "Fakturan har redan betalats"}
                    :invoice-createerror-originclash    {:fi "Sama lasku eri lähteestä on jo olemassa"
                                                         :en "The same invoice from a different source already exists."
                                                         :sv "Samma faktura från en annan källa redan finns."}
                    :invoice-createerror-invalidamount  {:fi "Laskun summa ei ole sallittu"
                                                         :en "The amount of the invoice is not allowed."
                                                         :sv "Fakturans summa är inte tillåten."}
                    :invoice-createerror-duedateinpast  {:fi "Eräpäivän tulee olla tulevaisuudessa"
                                                         :en "Due-date needs to be in future."
                                                         :sv "Förfallodagarnas antal är inte tillåtet."}
                    :invoice-createerror-invalidduedays {:fi "Eräpäivien lukumäärä ei ole sallittu"
                                                         :en "The amount of the due dates is not allowed."
                                                         :sv "Förfallodagarnas antal är inte tillåtet."}}

   :yleiset        {:maksu-keskeytetty {:fi "Maksu keskeytetty"
                                        :en "Payment has been terminated."
                                        :sv "Betalning avbruten."}
                    :http-virhe        {:fi "Tietojen haku epäonnistui"
                                        :en "Information search failed."
                                        :sv "Sökning av uppgifterna misslyckades."}}

   :tutu-panel      {:otsikko {:fi "Maksutapahtumat"
                               :en "Payments"
                               :sv "Betalningar"}
                     :aliotsikko {:fi "Tutkinnon tunnustaminen"
                                  :en "Recognition of qualifications"
                                  :sv "Erkännande av examina"}
                     :maksulaatikko-otsikko-käsittely {:fi "Käsittelymaksu"
                                                       :en "Processing fee"
                                                       :sv "Behandlingsavgift"}
                     :maksulaatikko-otsikko-päätös {:fi "Päätösmaksu"
                                                    :en "Decision fee"
                                                    :sv "Beslutsavgift"}
                     :maksu-nappula {:fi "Siirry maksamaan"
                                     :en "Go to payment"
                                     :sv "Gå till betalning"}
                     :maksu-tila {:fi "Tila"
                                  :en "Status"
                                  :sv "Status"}
                     :maksu-summa {:fi "Määrä"
                                   :en "Amount"
                                   :sv "Summa"}
                     :maksu-eräpäivä {:fi "Eräpäivä"
                                      :en "Due date"
                                      :sv "Förfallodagen"}
                     :maksu-maksupäivä {:fi "Maksupäivä"
                                        :en "Payment date"
                                        :sv "Betalningsdag"}
                     :tila-käsittely {:fi "Hakemuksen käsittely"
                                      :en "Processing of the application"
                                      :sv "Behandling av ansökan"}
                     :tila-päätösmaksu {:fi "Päätösmaksu"
                                        :en "Decision fee"
                                        :sv "Beslutsavgift"}
                     :tila-käsittelymaksu {:fi "Käsittelymaksu"
                                           :en "Processing fee"
                                           :sv "Behandlingsavgift"}
                     }

   :maksu-tila      {:active  {:fi "Maksamatta"
                               :en "Unpaid"
                               :sv "Obetald"}
                     :paid    {:fi "Maksettu"
                               :en "Paid"
                               :sv "Betald"}
                     :overdue {:fi "Erääntynyt"
                               :en "Expired"
                               :sv "Förfallen"}}

   :invoice-not-found {:paid-oldsecret-header     {:fi "Linkki laskuun on vanhentunut"
                                                   :en "Link to the payment is too old"
                                                   :sv "Länk till betalning är för gammal"}
                       :processing-overdue-header {:fi "Hakemuksesi käsittelymaksun eräpäivä on mennyt umpeen"
                                                   :en "The due date for the processing fee of your application has passed"
                                                   :sv "Förfallodagen för behandlingsavgiften för din ansökan har gått ut"}
                       :processing-overdue-text-1 {:fi "Et ole suorittanut 70 euron käsittelymaksua eräpäivään mennessä. Hakemuksesi on rauennut. Jos haluat edelleen hakea tutkintosi tunnustamista, täytä hakulomake uudelleen."
                                                   :en "You have not paid the processing fee of EUR 70 by the due date. Your application has expired. If you still wish to apply for recognition of your qualification, please fill out the application form again."
                                                   :sv "Du har inte betalt behandlingsavgiften på 70 euro före förfallodagen. Din ansökan har förfallit. Om du vill ännu ansöka om erkännande av din examen, lämna in en ny ansökan."}
                       :processing-overdue-text-2 {:fi "Jos sinulla on kysyttävää, lähetä sähköpostia osoitteeseen "
                                                   :en "If you have any questions, please contact us by email at "
                                                   :sv "Om du har frågor, kontakta oss per e-post "}
                       :processing-overdue-text-3 {:fi "Linkki tutkintojen tunnustamisen etusivulle: "
                                                   :en "Link to the homepage of recognition of qualifications: "
                                                   :sv "Länk till webbsidor för erkännande av examina: "}
                       :decision-overdue-header   {:fi "Hakemuksesi päätösmaksun eräpäivä on mennyt umpeen"
                                                   :en "The due date for the decision fee of your application has passed"
                                                   :sv "Förfallodagen för beslutsavgiften för din ansökan har gått ut."}
                       :decision-overdue-text-1   {:fi "Et ole suorittanut päätösmaksua eräpäivään mennessä. Lähetämme sinulle päätösmaksusta erillisen laskun. Hakemasi päätös lähetetään sinulle vasta päätösmaksun suorittamisen jälkeen."
                                                   :en "You have not paid the decision fee by the due date. We will send you a separate invoice for the decision fee. We will not send the decision until after you have paid the decision fee."
                                                   :sv "Du har inte betalt beslutsavgiften före förfallodagen. Vi skickar dig en separat faktura för beslutsavgiften. Vi skickar dig beslutet först då du har betalat beslutsavgiften."}
                       :decision-overdue-text-2   {:fi "Maksu perustuu opetus- ja kulttuuriministeriön voimassa olevaan asetukseen Opetushallituksen suoritteiden maksullisuudesta. Maksu on ulosottokelpoinen ilman tuomiota tai päätöstä (valtion maksuperustelaki (150/1992) 11§ 1.mom.)."
                                                   :en "The fee is based on the Ministry of Education and Culture’s Decree on the Fees on the Services Provided by the Finnish National Agency for Education. The fee is enforceable without a judgement or a decision (Act on Criteria for Charges Payable to the State 150/1992, section 11, subsection 1)."
                                                   :sv "Avgiften är baserad på undervisnings- och kulturministeriets gällande förordning om Utbildningsstyrelsens avgiftsbelagda prestationer. Avgiften från indrivas utan dom eller beslut (lag om grunderna för avgifter till staten (150/1992 11 § 1 mom.)."}
                       :decision-overdue-text-3   {:fi "Laskuun liittyvissä kysymyksissä voit olla suoraan yhteydessä Opetushallituksen myyntilaskutukseen osoitteessa "
                                                   :en "With questions regarding the invoices, please contact our invoicing services directly at "
                                                   :sv "Om du har frågor om fakturan, vänligen kontakta Utbildningsstyrelsens fakturering per e-post "}
                       :decision-overdue-text-4   {:fi "Lisätietoja saat sähköpostitse osoitteesta "
                                                   :en "For more information, please contact us by email at "
                                                   :sv "Mer information får du per e-post "}
                       :invalid-secret-header     {:fi "Maksun tietoja ei löydy"
                                                   :en "The details of your payment cannot be found"
                                                   :sv "Uppgifterna om avgiften hittades inte"}
                       :invalid-secret-text       {:fi "Maksusi tila ei ole nähtävissä. Tilanteen selvittämiseksi ota yhteyttä Opetushallituksen Tutkintojen ja kieliosaamisen tunnustaminen -yksikköön sähköpostitse osoitteessa "
                                                   :en "The status of your payment is not available. To clear up the situation, please contact the Finnish National Agency for Education’s unit for recognition of qualifications and language proficiency, please contact us by email at "
                                                   :sv "Status för avgiften är inte tillgänglig. För att reda ut situationen, kontakta Utbildningsstyrelsens enhet för erkännande av examina och språkkunskaper per e-post "}}


   :tutu-panel-ohje {:kasittely-maksamatta {:fi "Sinun tulee maksaa käsittelymaksu ennen kuin hakemuksesi otetaan käsittelyyn. Huomaathan, että sinun tulee myös maksaa erikseen päätösmaksu ennen kuin saat päätöksen."
                                            :en "You need to pay the processing fee before your application is taken into processing. Please note, that you also need to pay a separate decision fee before the decision is sent to you."
                                            :sv "Du måste betala behandlingsavgiften före din ansökan tas till behandling. Kom ihåg att du måste också betala en separat beslutsavgift före du får beslutet."}
                     :kasittely-maksettu   {:fi "Olet maksanut käsittelymaksun. Hakemuksesi siirtyy seuraavaksi käsittelyyn. Kun olemme tehneet päätöksen hakemuksestasi, lähetämme sinulle sähköpostitse maksuohjeet päätösmaksusta. Lähetämme päätöksen vasta, kun olet maksanut päätösmaksun. Hakemusten keskimääräinen käsittelyaika on 2–3 kuukautta. Jos käytät yhteiskäyttöistä tietokonetta, muista tyhjentää selaimen välimuisti ja kirjautua ulos koneelta. Voit nyt sulkea tämän ikkunan."
                                            :en "You have paid the processing fee. A confirmation about the transaction has been sent to your email. Please note, that you need to also pay a separate decision fee before the decision is sent to you. You will receive a notification by email when your application has been processed. If you are using a computer that is in common use, remember to empty the cache and log out from the computer. You can now close this window."
                                            :sv "Du har betalat behandlingsavgiften. Du har fått en bekräftelse om betalningen till din epost. Kom ihåg att du måste också betala en separat beslutsavgift före du får beslutet. Vi skickar dig ett meddelande per epost när din ansökan har behandlats. Om du använder en dator som är i gemensamt bruk, kom ihåg att tomma cacheminnen och logga ut ur datorn. Du kan nu stänga det här fönstret."}
                     :paatos-maksamatta    {:fi "Hakemuksesi on nyt käsitelty. Siirry maksamaan päätösmaksu."
                                            :en "Your application has been processed. Proceed to the payment of the decision fee."
                                            :sv "Din ansökan har nu behandlats. Gå till betalning av beslutsavgiften."}
                     :paatos-maksettu      {:fi "Olet maksanut päätösmaksun. Lähetämme päätöksen sinulle mahdollisimman pian. Jos käytät yhteiskäyttöistä tietokonetta, muista tyhjentää selaimen välimuisti ja kirjautua ulos koneelta. Voit nyt sulkea tämän ikkunan."
                                            :en "The decision fee for your application has been paid. A confirmation about the transaction has been sent to your email. If you are using a computer that is in common use, remember to empty the cache and log out from the computer. You can now close this window."
                                            :sv "Du har betalat beslutsavgiften. Du har fått en bekräftelse om betalningen till din epost. Om du använder en dator som är i gemensamt bruk, kom ihåg att tomma cacheminnen och logga ut ur datorn. Du kan nu stänga det här fönstret."}}

   })
