(ns maksut.translations)

(def maksut-ui-local-translations
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
   :KkHakemusmaksuPanel.title          {:fi "Hakemusmaksu"
                                        :en "Application fee"
                                        :sv "Ansökningsavgift"}
   :KkHakemusmaksuPanel.maksettu       {:fi "Olet nyt maksanut hakemusmaksun. Saat kuitin maksusta sähköpostiisi."
                                        :en "You have now paid the application fee. You will receive a receipt of the payment to your email."
                                        :sv "Du har nu betalat ansökningsavgiften. Du får ett kvitto på betalningen till din e-post."}
   :KkHakemusmaksuPanel.maksettu2      {:fi "Maksu on voimassa kaikkiin koulutuksiin, jotka alkavat lukukautena:"
                                        :en "The payment is valid to all study programmes that start in the academic term"
                                        :sv "Avgiften gäller för alla utbildningar som börjar under terminen:"}
   :KkHakemusmaksuPanel.maksettu3      {:fi "Sinun ei siis tarvitse maksaa hakemusmaksua uudelleen, jos haet muihin samana lukukautena alkaviin koulutuksiin."
                                        :en "You do not need to pay the application fee again, if you apply to other study programmes staring in the same academic term."
                                        :sv "Du behöver alltså inte betala ansökningsavgiften på nytt om du söker till andra utbildningar som börjar samma termin."}
   :KkHakemusmaksuPanel.maksettu4      {:fi "Huomioithan, että hakemusmaksun maksaminen ei vielä tarkoita, että sinut hyväksytään koulutukseen."
                                        :en "Please note that paying the application fee does not mean that you will be automatically offered admission."
                                        :sv "Observera att betalning av ansökningsavgift ännu inte innebär att du blir antagen till utbildningen."}
   :KkHakemusmaksuPanel.maksamatta     {:fi "Siirry maksamaan hakemusmaksu. Kun olet maksanut hakemusmaksun, saat kuitin maksusta sähköpostiisi."
                                        :en "Proceed to pay the application fee. You will receive a receipt for your payment in your email after paying the application fee."
                                        :sv "Gå vidare till att betala ansökningsavgiften. När du har betalat ansökningsavgiften får du ett kvitto på betalningen till din e-post."}
   :KkHakemusmaksuPanel.maksamatta2    {:fi "Huomioithan, että hakemusmaksun maksaminen ei vielä tarkoita, että sinut hyväksytään koulutukseen."
                                        :en "Please note that paying the application fee does not mean that you will be automatically offered admission."
                                        :sv "Observera att betalning av ansökningsavgift ännu inte innebär att du blir antagen till utbildningen."}
   :KkHakemusmaksuPanel.eraantynyt     {:fi "Hakemusmaksun määräaika on erääntynyt, etkä voi enää maksaa hakemusmaksua. Hakemustasi ei käsitellä, etkä voi tulla valituksi koulutukseen. Mikäli hakuaikaa on vielä jäljellä, voit täyttää uuden hakemuksen."
                                        :en "The due date for your application fee payment has expired. You can no longer pay the application fee. Your application will not be reviewed, and you cannot be offered admission. If the application period is still ongoing, you can fill in and send a new application."
                                        :sv "Tidsfristen för ansökningsavgiften har gått ut och du kan inte längre betala avgiften. Din ansökan behandlas inte och du kan inte bli antagen till utbildningen. Om ansökningstiden ännu pågår kan du fylla i en ny ansökan."}
   :KkHakemusmaksuPanel.mitatoity      {:fi "Olet jo maksanut hakemusmaksun toisessa haussa samana lukukautena alkavaan koulutukseen. Maksu on voimassa kaikkiin koulutuksiin, jotka alkavat lukukautena:"
                                        :en "You have already paid the application fee for another application starting in the same academic term. The payment is valid to all study programmes that start in the academic term:"
                                        :sv "Du har redan betalat ansökningsavgiften för en annan ansökan till en utbildning som börjar samma termin. Avgiften gäller för alla utbildningar som börjar under terminen:"}
   :KkHakemusmaksuPanel.mitatoity2     {:fi "Sinun ei siis tarvitse maksaa enää hakemusmaksua uudelleen."
                                        :en "You do not need to pay the application fee again."
                                        :sv "Du behöver alltså inte betala ansökningsavgiften på nytt om du söker till andra utbildningar som börjar samma termin."}
   :KkHakemusmaksuPanel.aloituskausi   {:fi "Alkamiskausi"
                                        :en "Start term"
                                        :sv "Starttermin"}
   :KkHakemusmaksuPanel.kausi_s        {:fi "syksy"
                                        :en "autumn"
                                        :sv "höst"}
   :KkHakemusmaksuPanel.kausi_k        {:fi "kevät"
                                        :en "spring"
                                        :sv "vår"}
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
   :Maksu.invalidated                  {:fi "Ei vaadittu"
                                        :en "Not required"
                                        :sv "SV: Not required"}
   :Maksu.summa                        {:fi "Määrä"
                                        :en "Amount"
                                        :sv "Summa"}
   :Maksu.eräpäivä                     {:fi "Eräpäivä"
                                        :en "Due date"
                                        :sv "Förfallodagen"}
   :Maksu.maksupäivä                   {:fi "Maksupäivä"
                                        :en "Payment date"
                                        :sv "Betalningsdag"}
   :ExpiredPanel.header                {:fi "Maksu on vanhentunut"
                                        :sv "Betalningen har gått ut"
                                        :en "Payment has expired"}
   :ExpiredPanel.body1                 {:fi "Et voi maksaa maksua tämän linkin kautta enää."
                                        :en "You can no longer make the payment through this link."
                                        :sv "Du kan inte längre betala via denna länk."}
   :ExpiredPanel.body2                 {:fi "Lähetä sähköpostia osoitteeseen "
                                        :en "Please send an email to "
                                        :sv "Vänligen skicka ett e-postmeddelande till "}
   :NotFound.header                    {:fi "Maksun tietoja ei löydy"
                                        :en "Payment information not found"
                                        :sv "Betalningsinformation hittades inte"}
   :NotFound.body1                     {:fi "Emme löytäneet maksusi tietoja."
                                        :sv "Vi kunde inte hitta din betalningsinformation."
                                        :en "We could not find your payment information."}
   :NotFound.body2                     {:fi "Ota yhteyttä Opetushallitukseen. Löydät palveluosoitteen saamastasi maksulinkkiviestistä."
                                        :en "Please contact the Finnish National Agency for Education. You can find the service address in the email with the payment link."
                                        :sv "Vänligen kontakta Utbildningsstyrelsen. Du hittar tjänsteadressen i betalningslänksmeddelandet du fick."}
   :Error.header                       {:fi "Tapahtui virhe"
                                        :en "An error occurred"
                                        :sv "Ett fel inträffade"}
   :PaymentError.header                {:fi "Virhe maksussa"
                                        :sv "Fel i betalningen"
                                        :en "Error in payment"}
   :PaymentError.body1                 {:fi "Maksaminen epäonnistui."
                                        :sv "Betalningen misslyckades."
                                        :en "Payment failed."}
   :PaymentError.body2                 {:fi "Palaa Maksutapahtumat-sivulle ja tarkista maksusi tilanne. Jos maksua ei ole maksettu, yritä maksamista myöhemmin uudelleen."
                                        :sv "Gå tillbaka till Betalningar-sidan och kontrollera statusen för din betalning. Om betalningen inte har genomförts, försök igen senare."
                                        :en "Return to the Payments page and check the status of your payment. If the payment has not been made, try again later."}
   :PaymentError.body3                 {:fi "Tarvittaessa lähetä sähköpostia osoitteeseen "
                                        :sv "Vid behov, skicka ett e-postmeddelande till "
                                        :en "If necessary, please send an email to "}
   :PaymentError.returnButton          {:fi "Palaa Maksutapahtumat-sivulle"
                                        :sv "Gå tillbaka till Betalningar-sidan"
                                        :en "Return to Payments page"}})

(def local-translations
  (let [kuitti-common {:oph                    {:fi "Opetushallitus"
                                                :en "Finnish National Agency for Education"
                                                :sv "Utbildningsstyrelsen"}
                       :address1               {:fi "Hakaniemenranta 6"
                                                :en "Hakaniemenranta 6"
                                                :sv "Hagnäskajen 6"}
                       :address2               {:fi "PL 380, 00531 Helsinki"
                                                :en "P.O. Box 380, 00531 Helsinki, Finland"
                                                :sv "PB 380, 00531 Helsingfors"}
                       :contact-details        {:fi "Puhelin: +358 29 533 1000"
                                                :en "Telephone: +358 29 533 1000"
                                                :sv "Telefon: +358 29 533 1000"}
                       :business-id            {:fi "Y-tunnus: 2769790-1"
                                                :en "Business ID: 2769790-1"
                                                :sv "FO-nummer: 2769790-1"}
                       :name-desc              {:fi "Nimi"
                                                :en "Name"
                                                :sv "Namn"}
                       :payment-reference-desc {:fi "Maksuviite"
                                                :en "Payment reference"
                                                :sv "Betalningsreferens"}
                       :date-of-purchase-desc  {:fi "Maksupäivä"
                                                :en "Payment date"
                                                :sv "Betalningsdatum"}
                       :description            {:fi "Kuvaus"
                                                :en "Description"
                                                :sv "Beskrivning"}
                       :units                  {:fi "Määrä"
                                                :en "Quantity"
                                                :sv "Kvantitet"}
                       :vat                    {:fi "ALV"
                                                :en "VAT"
                                                :sv "MOMS"}
                       :unit-price             {:fi "Yksikköhinta"
                                                :en "Unit price"
                                                :sv "Jämförpris"}
                       :total-paid             {:fi "YHTEENSÄ MAKSETTU"
                                                :en "TOTAL PAID"
                                                :sv "TOTALT BETALT"}}]
    {:email           {:subject-prefix {:fi "Opetushallitus"
                                        :sv "Utbildningsstyrelsen"
                                        :en "Finnish National Agency for Education"}}

     :email-käsittely {:otsikko                        {:fi "Käsittelymaksusi on vastaanotettu"
                                                        :en "Your processing fee has been received"
                                                        :sv "Din behandlingsavgift har emottagits"}
                       :käsittely-selitys              {:fi "Olet maksanut käsittelymaksun. Olet saanut kuitin maksustasi erillisellä sähköpostilla. Voit käydä tarkastelemassa maksusi tietoja aiemmin sähköpostitse saamasi linkin kautta. Linkki toimii 14 vuorokauden ajan siitä, kun lähetimme maksupyynnön. Sen jälkeen linkki sulkeutuu tietosuojasyistä."
                                                        :en "You have paid the processing fee. You have received a receipt for your payment in a separate email. You can view the details of your payment through the link you have received in your email. The link will work for 14 days from the date when we sent the payment request to you. After that the link will be closed for data protection reasons."
                                                        :sv "Du har betalat behandlingsavgiften. Kvittot på betalningen har du fått som ett separat epost. Du kan kontrollera uppgifterna om den betalda avgiften via länken som du har fått tidigare med e-post. Länken fungerar i 14 dygn efter att vi har skickat dig begäran om behandlingsavgiften. Därefter stängs länken på grund av datasäkerhetsskäl."}
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
                                                        :sv "När vi har fattat beslut om din ansökan, skickar vi dig betalningsanvisningarna för beslutsavgiften. Vi skickar dig beslutet först då du har betalat beslutsavgiften. Avgiften får indrivas utan dom eller beslut (lag om grunderna för avgifter till staten (150/1992 11 § 1 mom.). Mer information om ansökan och avgifterna finns på vår webbplats:"}
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
                       :päätös-selitys      {:fi "Olet maksanut päätösmaksun. Olet saanut kuitin maksustasi erillisellä sähköpostilla. Lähetämme päätöksen sinulle mahdollisimman pian. Jos et ole saanut päätöstä kolmen työpäivän sisällä, ota meihin yhteyttä sähköpostitse osoitteessa recognition@oph.fi."
                                             :en "You have paid the decision fee. You have received a receipt for your payment in a separate email. We will send the decision to you as soon as possible. If you have not received the decision in three working days, please contact us by email at recognition@oph.fi."
                                             :sv "Du har betalat beslutsavgiften. Kvittot på betalningen har du fått som ett separat epost. Vi skickar beslutet till dig så snart som möjligt. Om du inte har fått beslutet inom tre arbetsdagar, vänligen kontakta oss per e-post: recognition@oph.fi."}
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

     :kuitti          (merge
                        kuitti-common
                        {:käsittely       {:fi "Opetushallitus Tutkintojen tunnustaminen Hakemuksen käsittelymaksu"
                                           :en "Finnish National Agency for Education Recognition of qualifications Processing fee"
                                           :sv "Utbildningsstyrelsen Erkännande av examina Behandlingsavgift"}
                         :päätös          {:fi "Opetushallitus Tutkintojen tunnustaminen Päätösmaksu"
                                           :en "Finnish National Agency for Education Recognition of qualifications Decision fee"
                                           :sv "Utbildningsstyrelsen Erkännande av examina Beslutsavgift"}
                         :käsittely-lr    {:fi "Opetushallitus\nTutkintojen tunnustaminen\nHakemuksen käsittelymaksu"
                                           :en "Finnish National Agency for Education\nRecognition of qualifications\nProcessing fee"
                                           :sv "Utbildningsstyrelsen\nErkännande av examina\nBehandlingsavgift"}
                         :päätös-lr       {:fi "Opetushallitus\nTutkintojen tunnustaminen\nPäätösmaksu"
                                           :en "Finnish National Agency for Education\nRecognition of qualifications\nDecision fee"
                                           :sv "Utbildningsstyrelsen\nErkännande av examina\nBeslutsavgift"}
                         :otsikko         {:fi "Kuitti tutkintojen tunnustamisen maksusta"
                                           :en "Receipt for payment of the fee for recognition of qualifications"
                                           :sv "Betalningskvitto på avgiften för erkännande av examina"}
                         :body-otsikko    {:fi "Tutkintojen tunnustaminen: kuitti"
                                           :en "Recognition of qualifications: receipt"
                                           :sv "Erkännande av examina: kvitto"}
                         :vat-explanation {:fi "Tutkintojen tunnustamisen maksut ovat opetus- ja kulttuuriministeriön asetuksella Opetushallituksen suoritteiden maksullisuudesta (137/2022) määrättyjä maksuja, joista ei peritä arvonlisäveroa."
                                           :en "The fees for recognition of qualifications are based on the Ministry of Education and Culture’s Decree on the Fees on the Services Provided by the Finnish National Agency for Education (137/2022). The fees are exempted from VAT."
                                           :sv "Avgifterna för erkännande av examina är föreskrivna i undervisnings- och kulturministeriets förordning om Utbildningsstyrelsens avgiftsbelagda prestationer (137/2022) och är momsfria."}})

     :astukuitti      (merge
                        kuitti-common
                        {:otsikko         {:fi "Kuitti maksusta"
                                           :en "Receipt for payment"
                                           :sv "Betalningskvitto på avgiften"}
                         :body-otsikko    {:fi ": kuitti"
                                           :en ": receipt"
                                           :sv ": kvitto"}
                         :vat-explanation {:fi "Hakemuksestasi peritty maksu on opetus- ja kulttuuriministeriön asetuksella Opetushallituksen suoritteiden maksullisuudesta (1188/2023) määrätty maksu. Lausuntojen maksuun sisältyy arvonlisäverolain mukainen arvonlisävero."
                                           :en "The fees for recognition of qualifications are based on the Ministry of Education and Culture’s Decree on the Fees on the Services Provided by the Finnish National Agency for Education (1188/2023). The payment of statements includes value added tax in accordance with the Value Added Tax Act."
                                           :sv "Avgifterna för offentligrättsliga prestationer är föreskrivna i undervisnings- och kulturministeriets förordning om Utbildningsstyrelsens avgiftsbelagda prestationer (1188/2023). I avgiften ingår moms enligt mervärdesskattelagen."}})

     :kkmaksukuitti   (merge
                        kuitti-common
                        {:otsikko         {:fi "Kuitti hakemusmaksusta"
                                           :en "Receipt for payment of the fee for application"
                                           :sv "Betalningskvitto på avgiften"}
                         :body-otsikko    {:fi ": kuitti hakemusmaksusta"
                                           :en ": receipt"
                                           :sv ": kvitto"}
                         :selite          {:fi "Hakemusmaksu"
                                           :en "Application fee"
                                           :sv "Ansökningsavgift"}
                         :vat-explanation {:fi "Hakemuksestasi peritty maksu on opetus- ja kulttuuriministeriön asetuksella Opetushallituksen suoritteiden maksullisuudesta (1188/2023) määrätty maksu."
                                           :en "The fees for recognition of qualifications are based on the Ministry of Education and Culture’s Decree on the Fees on the Services Provided by the Finnish National Agency for Education (1188/2023). The fees are exempted from VAT."
                                           :sv "Ansökningsavgiften är föreskriven i undervisnings- och kulturministeriets förordning om Utbildningsstyrelsens avgiftsbelagda prestationer (1188/2023)."}})}))
