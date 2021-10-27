(ns maksut.i18n.translations)

(def local-translations
  {:rest           {:invoice-notfound {:fi "Laskua ei löydy"}
                    :invoice-notfound-overdue {:fi "Maksulinkki on väärä tai vanhentunut. Huomaathan että käsittelymaksu tulee suorittaa 14 päivän sisällä tai hakemus peruuntuu. Tämän jälkeen sinun tulee tehdä uusi hakemus jatkaaksi."}
                    :invoice-notfound-secret {:fi "Maksulinkki on väärä."}
                    :invoice-invalidstate-overdue {:fi "Lasku on erääntynyt"}
                    :invoice-invalidstate-paid {:fi "Lasku on jo maksettu"}
                    :invoice-createerror-originclash {:fi "Sama lasku eri lähteestä on jo olemassa"}
                    :invoice-createerror-invalidamount {:fi "Laskun summa ei ole sallittu"}
                    :invoice-createerror-invalidduedays {:fi "Eräpäivien lukumäärä ei ole sallittu"}
                    }

   :yleiset        {;MAKSUT
                    :maksu-keskeytetty {:fi "Maksu keskeytetty"}

                    :ei-valittavia-kohteita {:fi "Valittavia kohteita ei löytynyt"}
                    :vahvista-poisto        {:fi "Vahvista poisto"}
                    :peruuta                {:fi "Peruuta"}
                    :hakulomake             {:fi "Hakulomake"}
                    :tallenna               {:fi "Tallenna"}
                    :hakuajat               {:fi "Hakuajat"}
                    :muokkaa-lomaketta      {:fi "Muokkaa lomaketta"}
                    :muokkaa-hakua          {:fi "Muokkaa hakua"}
                    :pakolliset-kentat      {:fi "* merkityt kentät ovat pakollisia"}
                    :http-virhe             {:fi "Tietojen haku epäonnistui"}
                    :arkistoitu             {:fi "Arkistoitu"}}

   })
