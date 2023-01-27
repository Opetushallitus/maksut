(ns maksut.translations)

(def local-translations
  {
   :email           {:subject-prefix {:fi "Opintopolku"
                                      :sv "Studieinfo"
                                      :en "Studyinfo"}}

   :email-käsittely {:otsikko                        {:fi "Käsittelymaksusi on vastaanotettu"
                                                      :en "Your processing fee has been received"
                                                      :sv "Din behandlingsavgift har emottagits"}
                     :käsittely-selitys              {:fi "Olet maksanut käsittelymaksun. Voit käydä tarkastelemassa maksusi tietoja aiemmin sähköpostitse saamasi linkin kautta. Linkki toimii 14 vuorokauden ajan siitä, kun lähetimme maksupyynnön. Sen jälkeen linkki sulkeutuu tietosuojasyistä."
                                                      :en "You have paid the processing fee. You can view the details of your payment through the link you have received in your email. The link will work for 14 days from the date when we sent the payment request to you. After that the link will be closed for data protection reasons."
                                                      :sv "Du har betalat behandlingsavgiften. Du kan kontrollera uppgifterna om den betalda avgiften via länken som du har fått tidigare med e-post. Länken fungerar i 14 dygn efter att vi har skickat dig begäran om behandlingsavgiften. Därefter stängs länken på grund av datasäkerhetsskäl."}
                     :käsittely-väliotsikko-1-1      {:fi "1.  Jos hait päätöstä "
                                                      :en "1.  If you have applied for "
                                                      :sv "1.  Om du har ansökt om "}
                     :käsittely-väliotsikko-1-2      {:fi "tutkintojen tunnustamisesta:"
                                                      :en "recognition of qualifications:"
                                                      :sv "erkännande av examina:"}
                     :käsittely-väliotsikko-2-1      {:fi "2.	Jos olet aiemmin saanut Opetushallituksen ehdollisen päätöksen ja hait nyt "
                                                      :en "2.	If you have received a conditional decision from the Finnish National Agency for Education earlier and have now applied for "
                                                      :sv "2.  Om du har ansökt om "}
                     :käsittely-väliotsikko-2-2      {:fi "lopullista päätöstä:"
                                                      :en "a final decision:"
                                                      :sv "ett slutligt beslut:"}
                     :information-url                {:fi "https://www.oph.fi/fi/palvelut/tutkintojen-tunnustaminen"
                                                      :en "https://www.oph.fi/en/services/recognition-and-international-comparability-qualifications"
                                                      :sv "https://www.oph.fi/sv/tjanster/erkannande-av-examina"}
                     :hakemusnumero                  {:fi "Hakemusnumero"
                                                      :en "Application number"
                                                      :sv "Ansökningsnummer"}
                     :käsittely-päätös-otsikko       {:fi "Hakemuksen käsittely ja myöhemmin maksettava päätösmaksu"
                                                      :en "Processing of the application and the decision fee due later"
                                                      :sv "Behandling av ansökan och beslutsavgiften som skall betalas senare"}
                     :käsittely-päätös-selitys       {:fi "Hakemuksesi siirtyy seuraavaksi käsittelyyn. Olemme sinuun tarvittaessa yhteydessä sähköpostitse. Hakemusten keskimääräinen käsittelyaika on 2–3 kuukautta."
                                                      :en "Your application will now be taken into processing. If necessary, we will contact you by email. The average processing time for applications is between 2 and 3 months."
                                                      :sv "Din ansökan tas nu till behandling. Vi kontaktar dig vid behov per epost. Den genomsnittliga behandlingstiden för ansökningarna är 2–3 månader."}
                     :käsittely-ehdollinen-selitys-1 {:fi "Hakemuksesi siirtyy seuraavaksi käsittelyyn. Olemme sinuun tarvittaessa yhteydessä sähköpostitse."
                                                      :en "Your application will now be taken into processing. If necessary, we will contact you by email."
                                                      :sv "Din ansökan tas nu till behandling. Vi kontaktar dig vid behov per epost."}
                     :käsittely-ehdollinen-selitys-2 {:fi "Sinulta ei peritä erillistä päätösmaksua. Kun olemme tehneet päätöksen hakemuksestasi, lähetämme sähköisesti allekirjoitetun päätöksen sinulle sähköpostitse tai postitse."
                                                      :en "A separate decision fee will not be charged. After a decision on your application has been made, we will send you the electronically signed decision by email or mail."
                                                      :sv "En separat beslutsavgift tas inte ut. När vi har fattat beslut om din ansökan, skickar vi dig det elektroniskt undertecknade beslutet per e-post eller per post."}
                     :käsittely-päätös-laki          {:fi "Kun olemme tehneet päätöksen hakemuksestasi, lähetämme sinulle maksuohjeet päätösmaksusta. Lähetämme päätöksen vasta, kun olet maksanut päätösmaksun. Maksu on ulosottokelpoinen ilman tuomiota tai päätöstä (valtion maksuperustelaki (150/1992) 11§ 1.mom.). Lisätietoja hakemisesta ja maksuista on nettisivuillamme:"
                                                      :en "When we have made the decision on your application, we will send you the instructions for the payment of the decision fee. We will not send the decision until after you have paid the decision fee. The fee is enforceable without a judgement or a decision (Act on Criteria for Charges Payable to the State 150/1992, section 11, subsection 1). More information on applying and the fees is available on our website:"
                                                      :sv "När vi har fattat beslut om din ansökan, skickar vi dig betalningsanvisningarna för beslutsavgiften. Vi skickar dig beslutet först då du har betalat beslutsavgiften. Avgiften från indrivas utan dom eller beslut (lag om grunderna för avgifter till staten (150/1992 11 § 1 mom.). Mer information om ansökan och avgifterna finns på vår webbplats:"}
                     :älä-vastaa-emailaa             {:fi "Älä vastaa tähän viestiin. Jos sinulla on kysyttävää, lähetä meille sähköpostia osoitteeseen"
                                                      :en "This is an automatically generated email, please do not reply. If you have any questions, please send us an email at"
                                                      :sv "Svara inte på detta meddelande, det har skickats automatiskt. Om du har frågor, vänligen kontakta oss per epost via"}
                     :allekirjoitus-alku             {:fi "Ystävällisin terveisin"
                                                      :en "Best regards"
                                                      :sv "Med vänliga hälsningar,"}
                     :allekirjoitus-loppu            {:fi "Opetushallitus"
                                                      :en "Finnish National Agency for Education"
                                                      :sv "Utbildningsstyrelsen"}}

   :email-päätös    {:otsikko             {:fi "Päätösmaksusi on vastaanotettu"
                                           :en "Your decision fee has been received"
                                           :sv "Din beslutsavgift har emottagits"}
                     :päätös-selitys      {:fi "Olet maksanut päätösmaksun. Lähetämme päätöksen sinulle mahdollisimman pian. Jos et ole saanut päätöstä kolmen työpäivän sisällä, ota meihin yhteyttä sähköpostitse osoitteessa recognition@oph.fi."
                                           :en "You have paid the decision fee. We will send the decision to you as soon as possible. If you have not received the decision in three working days, please contact us by email at recognition@oph.fi."
                                           :sv "Du har betalat beslutsavgiften. Vi skickar beslutet till dig så snart som möjligt. Om du inte har fått beslutet inom tre arbetsdagar, vänligen kontakta oss per e-post: recognition@oph.fi."}
                     :tarkastelu-teksti   {:fi "Voit käydä tarkastelemassa maksusi tietoja aiemmin sähköpostitse saamasi linkin kautta. Linkki toimii 14 vuorokauden ajan siitä, kun lähetimme maksupyynnön. Sen jälkeen linkki sulkeutuu tietosuojasyistä."
                                           :en "You can view the details of your payment through the link you have received in your email. The link will work for 14 days from the date when we sent the payment request to you. After that the link will be closed for data protection reasons."
                                           :sv "Du kan kontrollera uppgifterna om den betalda avgiften via länken som du har fått tidigare med e-post. Länken fungerar i 14 dygn efter att vi har skickat dig begäran om beslutsavgiften. Därefter stängs länken på grund av datasäkerhetsskäl."}
                     :älä-vastaa-emailaa  {:fi "Älä vastaa tähän viestiin – viesti on lähetetty automaattisesti. Jos sinulla on kysyttävää, otathan meihin yhteyttä sähköpostitse osoitteessa"
                                           :en "This is an automatically generated email, please do not reply. If you have any questions, please send us an email at"
                                           :sv "Svara inte på detta meddelande, det har skickats automatiskt. Om du har frågor, vänligen kontakta oss per e-post:"}
                     :allekirjoitus-alku  {:fi "Ystävällisin terveisin"
                                           :en "Best regards"
                                           :sv "Med vänliga hälsningar"}
                     :allekirjoitus-loppu {:fi "Opetushallitus"
                                           :en "Finnish National Agency for Education"
                                           :sv "Utbildningsstyrelsen"}}

   :kuitti          {:käsittely           {:fi "Opetushallitus Tutkintojen tunnustaminen Hakemuksen käsittelymaksu"
                                           :en "Finnish National Agency for Education Recognition of qualifications Processing fee"
                                           :sv "Utbildningsstyrelsen Erkännande av examina Behandlingsavgift"}
                     :päätös              {:fi "Opetushallitus Tutkintojen tunnustaminen Päätösmaksu"
                                           :en "Finnish National Agency for Education Recognition of qualifications Decision fee"
                                           :sv "Utbildningsstyrelsen Erkännande av examina Beslutsavgift"}
                     :otsikko             {:fi "Kuitti"
                                           :en "Receipt"
                                           :sv "Kvitto"}
                     :oph                 {:fi "Opetushallitus"
                                           :en "Finnish National Agency for Education"
                                           :sv "Utbildningsstyrelsen"}
                     :address1            {:fi "Hakaniemenranta 6"
                                           :en "Hakaniemenranta 6"
                                           :sv "Hagnäskajen 6"}
                     :address2            {:fi "PL 380, 00531 Helsinki"
                                           :en "P.O. Box 380, 00531 Helsinki, Finland"
                                           :sv "PB 380, 00531 Helsingfors"}
                     :contact-details     {:fi "Puhelin: +358 29 533 1000"
                                           :en "Telephone: +358 29 533 1000"
                                           :sv "Telefon: +358 29 533 1000"}
                     :business-id         {:fi "Y-tunnus: 2769790-1"
                                           :en "Business ID: 2769790-1"
                                           :sv "FO-nummer: 2769790-1"}
                     :payment-reference-desc {:fi "Viite"
                                              :en "Payment reference"
                                              :sv "Betalningsreferens"}
                     :date-of-purchase-desc {:fi "Päiväys"
                                             :en "Date"
                                             :sv "Datum"}
                     :description         {:fi "Kuvaus"
                                           :en "Description"
                                           :sv "Beskrivning"}
                     :units               {:fi "Määrä"
                                           :en "Quantity"
                                           :sv "Kvantitet"}
                     :vat                 {:fi "ALV"
                                           :en "VAT"
                                           :sv "MOMS"}
                     :unit-price          {:fi "Yksikköhinta"
                                           :en "Unit price"
                                           :sv "Jämförpris"}
                     :total-paid          {:fi "YHTEENSÄ MAKSETTU"
                                           :en "TOTAL PAID"
                                           :sv "TOTALT BETALDA"}
                     :älä-vastaa-emailaa  {:fi "Älä vastaa tähän viestiin – viesti on lähetetty automaattisesti. Jos sinulla on kysyttävää, otathan meihin yhteyttä sähköpostitse osoitteessa"
                                           :en "This is an automatically generated email, please do not reply. If you have any questions, please send us an email at"
                                           :sv "Svara inte på detta meddelande, det har skickats automatiskt. Om du har frågor, vänligen kontakta oss per e-post:"}}})
