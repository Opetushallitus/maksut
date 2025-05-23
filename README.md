# Maksut

[![Build Status](https://travis-ci.org/Opetushallitus/maksut.svg?branch=master)](https://travis-ci.org/Opetushallitus/maksut)

* [Lokaali kehitys](#lokaali-kehitys)
* [Palvelun ajaminen paikallisesti testiympäristöä vasten](#palvelun-ajaminen-paikallisesti-testiympäristöä-vasten)
  * [Vain kerran tehtävät työvaiheet](#vain-kerran-tehtävät-työvaiheet)
  * [Käyttö](#käyttö)
    * [Palvelun käynnistäminen](#palvelun-ajaminen)
    * [Palvelun pysäyttäminen](#palvelun-pysäyttäminen)
    * [Palvelun logien tarkastelu](#palvelun-logien-tarkastelu)
    * [Palvelun komponenttien tilan tarkastelu](#palvelun-komponenttien-tilan-tarkastelu)
  * [Swagger](#swagger)
* [Testien ajaminen](#testien-ajaminen)
  * [Lint](#lint)
    * [Clojure -tiedostojen lint](#clojure--tiedostojen-lint)
* [REPL-yhteys palvelimeen ja selaimeen](#repl-yhteys-palvelimeen-ja-selaimeen)
* [Palvelun paikalliset osoitteet](#palvelun-paikalliset-osoitteet)
* [Tuotantokäyttö](#tuotantokäyttö)
  * [Palvelun uberjar -tiedoston luonti tuotantokäyttöä varten](#palvelun-uberjar--tiedoston-luonti-tuotantok%C3%A4ytt%C3%B6%C3%A4-varten)
  * [Palvelun ajaminen uberjar -tiedostosta](#palvelun-ajaminen-uberjar--tiedostosta)

## Lokaali kehitys

Paytrail-flowta voi kehittää/testata lokaalilla kannalla seuraavilla askelilla:

1. Varmista että ajossa on oikea Node-versio (Asenna nvm jos ei asennettu):

    ```bash
       nvm use
    ```

2. Lisää hosts-tiedostoon:

    ```bash
       127.0.0.1       maksut-local.test
    ```

3. Käynnistä sovellus shellissä:

   ```bash
   make start-local CONFIG=oph-configuration/config.dev.edn
   ```

4. Kirjaudu sovellukseen menemällä osoitteeseen: https://localhost:9000/maksut/auth/cas?ticket=abc (tikettiparametrin 
   arvolla ei ole väliä).


5. Mene swagger-ui:hin osoitteessa: https://localhost:9000/maksut/swagger, ja tee Maksut -> /maksut/api/lasku-tutu POST-kutsu
   (esimerkiksi) seuraavalla payloadilla:

   ```bash
    {
      "application-key": "12345",
      "first-name": "test",
      "last-name": "test",
      "email": "test@example.com",
      "amount": "11",
      "due-date": "2025-03-03",
      "index": 1
   }
   ```

   Huomaa että "application-key" -kentän tulee olla uniikki uuden laskun luomiseksi. Kutsu palauttaa seuraavan muotoisen vastauksen:

   ```bash
    {
      "order_id": "TTU12345-1",
      "first_name": "test",
      "last_name": "test",
      "amount": "123.00",
      "due_date": "2024-05-06",
      "status": "active",
      "secret": "VFRVYXRhcnUtMQZoGsstaDlfq3h5AU8Mv78nm0cvV01abrNuMvTlGK4j6DyA",
      "paid_at": ""
    }
    ```

6. Mene osoitteeseen: https://localhost:9000/maksut/?secret=<SECRET>&locale=fi (secret-parametrin arvo otetaan edellisen kutsun
   vastauksesta). Tästä voit nakutella flown läpi painamalla "Siirry maksamaan" ja valitsemalla Paytrailin puolella maksutavaksi OP:n.


7. Tapahtuman tuloksena lähetetyt mailit voi katsoa Mailcatcherista osoitteesta: http://localhost:1080/.


## Palvelun ajaminen paikallisesti testiympäristöä vasten

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

#### Clojure -tiedostojen lint

```sh
npm run lint:clj
```

### Backend-testit

Käynnistä ensin bäkkäri komennolla

```
make start-test CONFIG=oph-configuration/config.test.local-environment.edn
```

Aja sen jälkeen lein testit käyttäen lokaalia konfiguraatiotiedostoa:

```
CONFIG=oph-configuration/config.test.local-environment.edn lein test
```

### Playwright käyttöliittymätestit

Käynnistä backend kuten backend-testejä varten, sekä käyttöliittymä

```bash
cd src/maksut-ui
npm run start-test
```

Käynnistä testit (avaa käyttöliittymä lisäämällä --ui flag)

```bash
npx playwright test
```

Oikeaa Paytrailia vasten komennolla:

```bash
WITH_PAYTRAIL=TRUE npx playwright test
```

## REPL-yhteys palvelimeen ja selaimeen

REPL-yhteys palvelimelle avautuu sanomalla komentorivillä

```sh
lein repl :connect localhost:9034
```

## Palvelun paikalliset osoitteet

* Palvelun osoite: (http://localhost:9099/maksut)

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
