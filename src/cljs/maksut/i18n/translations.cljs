(ns maksut.i18n.translations)

(def local-translations
  {:rest-error     {:invoice-notfound                   {:fi "Laskua ei löydy"}
                    :invoice-notfound-secret            {:fi "Maksulinkki on väärä."}
                    :invoice-notfound-oldsecret         {:fi "Maksulinkki on vanhentunut. [TODO pidempi teksti]"}
                    :invoice-invalidstate-overdue       {:fi "Lasku on erääntynyt"}
                    :invoice-invalidstate-paid          {:fi "Lasku on jo maksettu"}
                    :invoice-createerror-originclash    {:fi "Sama lasku eri lähteestä on jo olemassa"}
                    :invoice-createerror-invalidamount  {:fi "Laskun summa ei ole sallittu"}
                    :invoice-createerror-invalidduedays {:fi "Eräpäivien lukumäärä ei ole sallittu"}}

   :yleiset        {:maksu-keskeytetty {:fi "Maksu keskeytetty"}
                    :http-virhe             {:fi "Tietojen haku epäonnistui"}}

   :tutu-panel      {:otsikko {:fi "Maksutapahtumat"}
                     :aliotsikko {:fi "Tutkinnon tunnustaminen"}
                     :maksulaatikko-otsikko-käsittely {:fi "Käsittelymaksu"}
                     :maksulaatikko-otsikko-päätös {:fi "Päätösmaksu"}
                     :maksu-nappula {:fi "Siirry maksamaan"}
                     :maksu-tila {:fi "Tila"}
                     :maksu-summa {:fi "Määrä"}
                     :maksu-eräpäivä {:fi "Eräpäivä"}
                     :maksu-maksupäivä {:fi "Maksupäivä"}
                     :tila-käsittely {:fi "Hakemuksen käsittely"}
                     :tila-päätösmaksu {:fi "Päätösmaksu"}
                     :tila-käsittelymaksu {:fi "Käsittelymaksu"}
                     }

   :maksu-tila      {:active  {:fi "Avoinna"}
                     :paid    {:fi "Maksettu"}
                     :overdue {:fi "Erääntynyt"}}

   :tutu-panel-ohje {:kasittely-maksamatta {:fi "Sinun tulee maksaa käsittelymaksu, ennen kuin hakemuksesi otetaan käsittelyyn. Huomaathan että sinun tulee myös maksaa erikseen päätösmaksu ennen kuin saat päätöksen."}
                     :kasittely-maksettu   {:fi "Käsittelymaksusi on maksettu onnistuneesti. Tapahtumasta on lähetetty sinulle myös vahvistus sähköpostiin. Huomaathan että sinun tulee myös maksaa erikseen päätösmaksu ennen kuin saat päätöksen. Saat sähköpostiin ilmoituksen kun hakemuksesi on käsitelty."}
                     :paatos-maksamatta    {:fi "Hakemuksesi on nyt käsitelty. Siirry maksamaan päätösmaksu."}
                     :paatos-maksettu      {:fi "Hakemuksesi päätösmaksu on maksettu. Tapahtumasta on lähetetty sinulle myös vahvistus sähköpostiin."}}

   })
