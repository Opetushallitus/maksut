# Maksut

[![Build Status](https://travis-ci.org/Opetushallitus/maksut.svg?branch=master)](https://travis-ci.org/Opetushallitus/maksut)

* [Palvelun ajaminen paikallisesti](#palvelun-ajaminen-paikallisesti)
  * [Vain kerran tehtävät työvaiheet](#vain-kerran-tehtävät-työvaiheet)
  * [Käyttö](#käyttö)
    * [Palvelun käynnistäminen](#palvelun-ajaminen)
    * [Palvelun pysäyttäminen](#palvelun-pysäyttäminen)
    * [Palvelun logien tarkastelu](#palvelun-logien-tarkastelu)
    * [Palvelun komponenttien tilan tarkastelu](#palvelun-komponenttien-tilan-tarkastelu)
  * [Swagger](#swagger)
* [Testien ajaminen](#testien-ajaminen)
  * [Lint](#lint)
    * [Clojure(Script) -tiedostojen lint](#clojurescript--tiedostojen-lint)
* [REPL-yhteys palvelimeen ja selaimeen](#repl-yhteys-palvelimeen-ja-selaimeen)
* [Palvelun paikalliset osoitteet](#palvelun-paikalliset-osoitteet)
* [Tuotantokäyttö](#tuotantokäyttö)
  * [Palvelun uberjar -tiedoston luonti tuotantokäyttöä varten](#palvelun-uberjar--tiedoston-luonti-tuotantok%C3%A4ytt%C3%B6%C3%A4-varten)
  * [Palvelun ajaminen uberjar -tiedostosta](#palvelun-ajaminen-uberjar--tiedostosta)

## Palvelun ajaminen paikallisesti

Kloonaa ja valmistele omien ohjeiden mukaan käyttökuntoon [local-environment](https://github.com/Opetushallitus/local-environment) -ympäristö.

### Vain kerran tehtävät työvaiheet

1. Valmistele palvelun konfiguraatio
   * Mene aiemmin kloonaamaasi [local-environment](https://github.com/Opetushallitus/local-environment) -repositoryyn.
   * Mikäli et ole vielä kertaakaan valmistellut local-environment -ympäristöä, tee se repositoryn ohjeiden mukaan.
   * Generoi konfiguraatiotiedosto palvelua varten. Generointi lataa S3:sta untuva-, hahtuva- ja pallero -ympäristöjen salaisuudet ja generoi jokaista ympäristöä vastaavan maksut-palvelun konfiguraation. Tee siis tämä local-environment -repoistoryssä.
   ```bash
   rm -f .opintopolku-local/.templates_compiled # Aja tämä komento, mikäli haluat pakottaa konfiguraation generoinnin
   make compile-templates
   ```
   * Konfiguraatiotiedostot löytyvät nyt local-environment -repositoryn alta hakemistosta `oph-configurations/{hahtuva,pallero,untuva}/oph-configuration/maksut.config.edn`
2. Valmistele nginx -containerin konfiguraatio
   * Mikäli käytät Mac OS -käyttöjärjestelmää, sinun ei tarvitse tehdä mitään.
   * Mikäli käytät Linuxia, etkä Mac OS -käyttöjärjestelmää, editoi tämän repositoryn `nginx/nginx.conf` -tiedostoa: korvaa kaikki `host.docker.internal` -osoitteet sillä IP-osoitteella, joka koneesi `docker0` -sovittimessa on käytössä. Tämän IP:n saat esimerkiksi komennolla `/sbin/ifconfig docker0` selville.
   * Mikäli käytät Windowsia (ja/tai WSL:ää) lisää kumpaisenkin hosts tiedostoon seuraavat: (Windows 127.0.0.1 tai WSL2 ::1 koska IPv6)

```  ::1       kehittajan-oma-kone.opintopolku.fi
     ::1       hakuperusteetdb
     ::1       valintalaskenta.kehittajan-oma-kone.untuvaopintopolku.fi
     ::1       valintalaskenta-ui.kehittajan-oma-kone.untuvaopintopolku.fi
     ::1       valintalaskentakoostepalvelu.kehittajan-oma-kone.untuvaopintopolku.fi
     ::1       valintaperusteet-service.kehittajan-oma-kone.untuvaopintopolku.fi
     ::1       valintaperusteet-ui.kehittajan-oma-kone.untuvaopintopolku.fi
     ::1       ataru-virkailija.kehittajan-oma-kone.untuvaopintopolku.fi
     ::1       ataru-hakija.kehittajan-oma-kone.untuvaopintopolku.fi
     ::1       ataru-figwheel-virkailija.kehittajan-oma-kone.untuvaopintopolku.fi
     ::1       ataru-figwheel-hakija.kehittajan-oma-kone.untuvaopintopolku.fi
     ::1       ataru-figwheel-hakija.kehittajan-oma-kone.testiopintopolku.fi
     ::1       ataru-redis.kehittajan-oma-kone.testiopintopolku.fi
     ::1       ataru-redis.kehittajan-oma-kone.hahtuvaopintopolku.fi
     ::1       liiteri.kehittajan-oma-kone.untuvaopintopolku.fi
     ::1       valinta-tulos-service.kehittajan-oma-kone.untuvaopintopolku.fi
     ::1       valintalaskenta.kehittajan-oma-kone.hahtuvaopintopolku.fi
     ::1       valintalaskenta-ui.kehittajan-oma-kone.hahtuvaopintopolku.fi
     ::1       valintalaskentakoostepalvelu.kehittajan-oma-kone.hahtuvaopintopolku.fi
     ::1       valintaperusteet-service.kehittajan-oma-kone.hahtuvaopintopolku.fi
     ::1       valintaperusteet-ui.kehittajan-oma-kone.hahtuvaopintopolku.fi
     ::1       ataru-virkailija.kehittajan-oma-kone.hahtuvaopintopolku.fi
     ::1       ataru-hakija.kehittajan-oma-kone.hahtuvaopintopolku.fi
     ::1       ataru-figwheel-virkailija.kehittajan-oma-kone.hahtuvaopintopolku.fi
     ::1       ataru-figwheel-hakija.kehittajan-oma-kone.hahtuvaopintopolku.fi
     ::1       liiteri.kehittajan-oma-kone.hahtuvaopintopolku.fi
     ::1       valinta-tulos-service.kehittajan-oma-kone.hahtuvaopintopolku.fi
     ::1       valintalaskenta.kehittajan-oma-kone.testiopintopolku.fi
     ::1       valintalaskenta-ui.kehittajan-oma-kone.testiopintopolku.fi
     ::1       valintalaskentakoostepalvelu.kehittajan-oma-kone.testiopintopolku.fi
     ::1       valintaperusteet-service.kehittajan-oma-kone.testiopintopolku.fi
     ::1       valintaperusteet-ui.kehittajan-oma-kone.testiopintopolku.fi
     ::1       ataru-virkailija.kehittajan-oma-kone.testiopintopolku.fi
     ::1       ataru-hakija.kehittajan-oma-kone.testiopintopolku.fi
     ::1       ataru-figwheel-virkailija.kehittajan-oma-kone.testiopintopolku.fi
     ::1       ataru-figwheel-hakija.kehittajan-oma-kone.testiopintopolku.fi
     ::1       liiteri.kehittajan-oma-kone.testiopintopolku.fi
     ::1       valinta-tulos-service.kehittajan-oma-kone.testiopintopolku.fi
     ::1       kehittajan-oma-kone.testiopintopolku.fi
     ::1       kehittajan-oma-kone.hahtuvaopintopolku.fi
     ::1       kehittajan-oma-kone.untuvaopintopolku.fi
     ::1       ataru-redis.kehittajan-oma-kone.untuvaopintopolku.fi
     ::1       toimimaton.virkailija-host-arvo.test.edn-tiedostosta
```
      
3. Konfiguroi SSH-client
   * Tarvitset SSH-tunnelia varten SSH-konfiguraatioosi tiedon useista porttiohjauksista. Maksut-palvelu (sekä Ataru-TuTu testailua esim. Untuvan ympäristöä varten) on tarpeen tunneloida ainakin seuraavat:
     * 28888:alb.untuvaopintopolku.fi:80
     * 55437:ataru.db.untuvaopintopolku.fi:5432
     * 55446:ataru.redis.untuvaopintopolku.fi:6379
     * 55099:maksut.db.untuvaopintopolku.fi:5432
     * 55088:lokalisointi.db.untuvaopintopolku.fi:5432

   * Kun olet ensin alustanut local-environment -ympäristön kohdan 1 mukaan, voit yksinkertaisimmillaan lisätä seuraavan rivin `~./ssh/config` -tiedostosi ensimmäiseksi riviksi:
   ```
   Include /polku/local-environment-repositoryysi/docker/ssh/config
   ```
   * Mikäli et halua määrittää kyseistä `Include` -direktiiviä, voit tarjota kyseiset porttiohjauskonfiguraatiot SSH-clientillesi jotenkin toisin.
   

### Maksuputken testaaminen testiympäristössä

Maksuputkea voi testata seuraavasti QA:lla

* Täytä TuTun lomake: https://testiopintopolku.fi/hakemus/74825f61-e561-447e-bef4-1bb5be4ea44a
    * Jos lomake on vaihtunut niin lomakkeen tunnisteen löytää myös atarun QA:n konfiguraatioista
* Ohjautuu ekaan maksuun, valitse Osuuspankki maksutavaksi
* Maksun jälkeen fakemaileriin (https://fakemailer.testiopintopolku.fi/) ilmestyy hakemuksen muokkauslinkki ja erillinen maili jossa kuitti
* Tämän jälkeen mene hakemusten käsittelyyn ja vaihda organisaatioksi Tutkintojen tunnustaminen
* Avaa lomakkeen hakemukset (HAKEMUS / Tutkintojen tunnustaminen)
* Avaa listauksesta edellä täytetty hakemus
    * käsittelyvaiheena pitäisi olla Käsittely maksettu
* Vaihda käsittelytilaksi "Päätösmaksu avoin", syötä maksun määrä ja viesti ja lähetä maksupyyntö
* Avaa fakemailerista hakijalle lähetetty sähköposti ja klikkaa sieltä maksulinkkiä
* Klikkaile maksuputken läpi
    * Hakemusten käsittelyssä pitäisi nyt näkyä molemmat maksukuitit

### Käyttö

Tämä on suositeltu tapa ajaa palvelua paikallisesti. Tässä ohjeessa oletetaan, että local-environment -repository löytyy maksut -hakemiston vierestä, samasta hakemistosta.

Käynnistetty palvelu on käytettävissä osoitteessa (http://localhost:9099/maksut).

Maksua voi testata paytrailissa valitsemalla Osuuspankin maksutavaksi.

Kun ajat palvelua, käynnistä aina ensin SSH-yhteys käyttämääsi ympäristöön. Oletuksena se on `untuva`:

```
ssh bastion.untuva
```

#### Palvelun ajaminen

```bash
export CONFIG='../local-environment/oph-configurations/local/maksut.config.edn'
# TAI
export CONFIG='../local-environment/oph-configurations/untuva/oph-configuration/maksut.config.edn'
make start-local    # Palvelun käynnistäminen
make reload         # Palvelun uudelleenlataaminen, ei uudellenkäynnistä docker-kontteja
make restart-local  # Palvelun uudelleenkäynnistys, uudelleenkäynnistää docker-kontit
```

#### Palvelun pysäyttäminen

```bash
make kill
```

#### Palvelun logien tarkastelu

```bash
make logs
```


#### Palvelun komponenttien tilan tarkastelu

```bash
make status
```

### Swagger
Swagger UI löytyy polusta `/maksut/swagger/index.html`

Swagger JSON löytyy polusta `/maksut/swagger.json` 

## Testien ajaminen

### Lint

#### Clojure(Script) -tiedostojen lint

```sh
npm run lint:clj
```

### Backend-testit

Käynnistä ensin bäkkäri komennolla

```
make start-test
```

Aja sen jälkeen lein testit käyttäen lokaalia konfiguraatiotiedostoa:

```
CONFIG=oph-configuration/config.test.local-environment.edn lein test
```

## REPL-yhteys palvelimeen ja selaimeen

REPL-yhteys palvelimelle avautuu sanomalla komentorivillä

```sh
lein repl :connect localhost:9034
```

REPL-yhteys selaimeen avautuu sanomalla em. REPL-yhteyden sisällä. Muistathan ensin avata selaimellasi palvelun (ks. osoite alta).

```clj
(shadow.cljs.devtools.api/nrepl-select :maksut)
```

## Palvelun paikalliset osoitteet

* Palvelun osoite: (http://localhost:9099/maksut)
* Palvelun Shadow CLJS -palvelimen osoite (http://localhost:9630)

## Tuotantokäyttö

### Palvelun uberjar -tiedoston luonti tuotantokäyttöä varten

Seuraava komento luo tämän repositoryn `target` -hakemistoon tiedoston `maksut.jar`.

```sh
lein with-profile prod uberjar
```

### Palvelun ajaminen uberjar -tiedostosta

```sh
CONFIG=/polku/palvelun/config-tiedostoon java -jar maksut.jar
```
