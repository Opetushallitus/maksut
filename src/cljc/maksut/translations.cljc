(ns maksut.translations)

(def local-translations
  {
   :email           {:subject-prefix {:fi "Opintopolku"
                                      :sv "Studieinfo"
                                      :en "Studyinfo"}}

   :email-käsittely {:otsikko           {:fi "Käsittelymaksusi on vastaanotettu"}
                     :information-url   {:fi "https://www.oph.fi/fi/palvelut/tutkintojen-tunnustaminen"}
                     :käsittely-selitys {:fi "Olet maksanut käsittelymaksun. Voit käydä tarkastelemassa maksusi tietoja aiemmin sähköpostitse saamasi linkin kautta. Linkki toimii 14 vuorokauden ajan siitä, kun lähetimme maksupyynnön. Sen jälkeen linkki sulkeutuu tietosuojasyistä."}
                     :hakemusnumero     {:fi "Hakemusnumero"}
                     :käsittely-päätös-otsikko {:fi "Hakemuksen käsittely ja myöhemmin maksettava päätösmaksu"}
                     :käsittely-päätös-selitys {:fi "Hakemuksesi siirtyy seuraavaksi käsittelyyn. Olemme sinuun tarvittaessa yhteydessä sähköpostitse. Hakemusten keskimääräinen käsittelyaika on 2–3 kuukautta."}
                     :käsittely-päätös-laki    {:fi "Kun olemme tehneet päätöksen hakemuksestasi, lähetämme sinulle maksuohjeet päätösmaksusta. Lähetämme päätöksen vasta, kun olet maksanut päätösmaksun. Maksu on ulosottokelpoinen ilman tuomiota tai päätöstä (valtion maksuperustelaki (150/1992) 11§ 1.mom.). Lisätietoja hakemisesta ja maksuista on nettisivuillamme:"}
                     :älä-vastaa-emailaa       {:fi "Älä vastaa tähän viestiin. Jos sinulla on kysyttävää, lähetä meille sähköpostia osoitteeseen"}
                     :allekirjoitus-alku       {:fi "Ystävällisin terveisin"}
                     :allekirjoitus-loppu      {:fi "Opetushallitus"}}

   :email-päätös    {:otsikko             {:fi "Päätösmaksusi on vastaanotettu"}
                     :päätös-selitys      {:fi "Olet maksanut päätösmaksun. Lähetämme päätöksen sinulle mahdollisimman pian."}
                     :tarkastelu-teksti   {:fi "Voit käydä tarkastelemassa maksusi tietoja aiemmin sähköpostitse saamasi linkin kautta. Linkki toimii 14 vuorokauden ajan siitä, kun lähetimme maksupyynnön. Sen jälkeen linkki sulkeutuu tietosuojasyistä."}
                     :älä-vastaa-emailaa  {:fi "Älä vastaa tähän viestiin. Jos sinulla on kysyttävää, lähetä meille sähköpostia osoitteeseen"}
                     :allekirjoitus-alku  {:fi "Ystävällisin terveisin"}
                     :allekirjoitus-loppu {:fi "Opetushallitus"}}

   })
