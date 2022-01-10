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
                                                         :sv "Förfallodagarnas antal är inte tillåtet."
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

   :maksu-tila      {:active  {:fi "Avoinna"
                               :en "Open"
                               :sv "Öppen"}
                     :paid    {:fi "Maksettu"
                               :en "Paid"
                               :sv "Betald"}
                     :overdue {:fi "Erääntynyt"
                               :en "Expired"
                               :sv "Förfallen"}}

   :tutu-panel-ohje {:kasittely-maksamatta {:fi "Sinun tulee maksaa käsittelymaksu, ennen kuin hakemuksesi otetaan käsittelyyn. Huomaathan että sinun tulee myös maksaa erikseen päätösmaksu ennen kuin saat päätöksen."
                                            :en "You need to pay the processing fee before your application is taken into processing. Please note, that you also need to pay a separate decision fee before the decision is sent to you."
                                            :sv "Du måste betala behandlingsavgiften före din ansökan tas till behandling. Kom ihåg att du måste också betala en separat beslutsavgift före du får beslutet."}
                     :kasittely-maksettu   {:fi "Käsittelymaksusi on maksettu onnistuneesti. Tapahtumasta on lähetetty sinulle myös vahvistus sähköpostiin. Huomaathan että sinun tulee myös maksaa erikseen päätösmaksu ennen kuin saat päätöksen. Saat sähköpostiin ilmoituksen kun hakemuksesi on käsitelty."
                                            :en "You have paid the processing fee. A confirmation about the transaction has been sent to your email. Please note, that you need to also pay a separate decision fee before the decision is sent to you. You will receive a notification by email when your application has been processed."
                                            :sv "Du har betalat behandlingsavgiften. Du har fått en bekräftelse om betalningen till din epost. Kom ihåg att du måste också betala en separat beslutsavgift före du får beslutet. Vi skickar dig ett meddelande per epost när din ansökan har behandlats."}
                     :paatos-maksamatta    {:fi "Hakemuksesi on nyt käsitelty. Siirry maksamaan päätösmaksu."
                                            :en "Your application has been processed. Proceed to the payment of the decision fee."
                                            :sv "Din ansökan har nu behandlats. Gå till betalning av beslutsavgiften."}
                     :paatos-maksettu      {:fi "Hakemuksesi päätösmaksu on maksettu. Tapahtumasta on lähetetty sinulle myös vahvistus sähköpostiin."
                                            :en "The decision fee for your application has been paid. A confirmation about the transaction has been sent to your email."
                                            :sv "Du har betalat beslutsavgiften. Du har fått en bekräftelse om betalningen till din epost."}}

   })
