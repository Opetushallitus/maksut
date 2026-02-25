(ns maksut.email.email-message-handling
  (:require [maksut.util.translation :refer [get-translation get-translation-ns]]
            [selmer.parser :as selmer]
            [selmer.filters :as filters]
            [clj-time.format :as f]
            [clj-time.coerce :as c]
            [clj-time.core :as t])
  (:import (fi.oph.viestinvalitys.vastaanotto.model
             Vastaanottajat$VastaanottajatBuilder
             Viesti
             ViestinvalitysBuilder)
           (java.util Optional)))

(def from-address "no-reply@opintopolku.fi")

(def finnish-datetime-formatter (f/with-zone (f/formatter "d.M.YYYY HH:mm")
                                             (t/time-zone-for-id "Europe/Helsinki")))

(def viestinvalitys-paakayttaja "APP_VIESTINVALITYS_OPH_PAAKAYTTAJA")

(def oph-organisaatio "1.2.246.562.10.00000000001")

(def lahettava-palvelu "maksut")

(def sailytysaika-5-vuotta (int 2000))

(def kayttorajoitukset
  (-> (ViestinvalitysBuilder/kayttooikeusrajoituksetBuilder)
      (.withKayttooikeus viestinvalitys-paakayttaja oph-organisaatio)
      (.build)))

(defn format-datetime-to-finnish-format [datetime]
  (f/unparse finnish-datetime-formatter datetime))

(defn finnish-datetime-from-long [timestamp-long]
  (format-datetime-to-finnish-format (c/from-long timestamp-long)))

(filters/add-filter! :datetime-format-with-dots-from-long
                     (fn [timestamp-long]
                       (finnish-datetime-from-long timestamp-long)))

(defn ->vastaanottajat [recipients]
  (let [builder ^Vastaanottajat$VastaanottajatBuilder (ViestinvalitysBuilder/vastaanottajatBuilder)
        with-recipient (fn ([builder recipient] (.withVastaanottaja builder (Optional/empty) recipient)))]
    (.build (reduce with-recipient builder recipients))))

(defn ->viesti ^Viesti [email-data body]
  (-> (ViestinvalitysBuilder/viestiBuilder)
      (.withOtsikko (:subject email-data))
      (.withHtmlSisalto body)
      (.withKielet (into-array ^String [(name (:lang email-data))]))
      (.withVastaanottajat (->vastaanottajat (:recipients email-data)))
      (.withKayttooikeusRajoitukset kayttorajoitukset)
      (.withLahettavaPalvelu lahettava-palvelu)
      (.withNormaaliPrioriteetti)
      (.withLahettaja (Optional/of "Opetushallitus") (:from email-data))
      (.withSailytysAika sailytysaika-5-vuotta) ; About 5 and a half years
      (.build)))

(defn- make-email ^Viesti [email-data render-file-fn]
  (when (seq (:recipients email-data))
    (let [template-params (:template-params email-data)
          body (render-file-fn template-params)]
      (->viesti email-data body))))

(defn make-email-data
  [recipient subject lang template-params]
  {:from from-address
   :recipients [recipient]
   :subject subject
   :lang lang
   :template-params template-params})


(defn- create-email ^Viesti [recipient locale trans-ns template-file & {:as params}]
   (let [lang                            (keyword locale)
         trans                           (partial get-translation lang)
         subject                         (str (trans :email/subject-prefix) ": " (trans (keyword (name trans-ns) (name :otsikko))))
         template-params                 (merge params
                                           (get-translation-ns lang trans-ns)
                                           {:lang              lang
                                            :information-email "recognition@oph.fi"
                                            })
         applicant-email-data            (make-email-data recipient subject lang template-params)
         render-file-fn                  (fn [template-params]
                                            (selmer/render-file template-file template-params))]
     (make-email
       applicant-email-data
       render-file-fn)))


(defn create-tutu-processing-email ^Viesti [recipient locale application-id]
  (create-email recipient
                locale
                :email-käsittely
                "templates/tutu_payment_processing.html"
                :application-id application-id))

(defn create-tutu-decision-email ^Viesti [recipient locale]
  (create-email recipient
                locale
                :email-päätös
                "templates/tutu_payment_decision.html"))

(defn create-payment-receipt ^Viesti
  [recipient locale first-name last-name payment-reference timestamp-millis
   total-amount items oppija-baseurl origin form-name haku-name]
  (create-email recipient
                locale
                (case origin
                  "tutu" :kuitti
                  "astu" :astukuitti
                  "kkhakemusmaksu" :kkmaksukuitti)
                "templates/payment_receipt.html"
                :first-name first-name
                :last-name last-name
                :payment-reference payment-reference
                :date-of-purchase timestamp-millis
                :total-amount total-amount
                :items items
                :lang locale
                :oppija-baseurl oppija-baseurl
                :form-name form-name
                :haku-name haku-name))
