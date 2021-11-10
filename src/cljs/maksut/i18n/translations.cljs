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

   })
