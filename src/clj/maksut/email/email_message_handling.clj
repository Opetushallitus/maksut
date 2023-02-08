(ns maksut.email.email-message-handling
  (:require [maksut.util.translation :refer [get-translation get-translation-ns]]
            [selmer.parser :as selmer]
            [selmer.filters :as filters]
            [clj-time.format :as f]
            [clj-time.coerce :as c]
            [clj-time.core :as t]))

(def from-address "no-reply@opintopolku.fi")

(def finnish-datetime-formatter (f/with-zone (f/formatter "d.M.YYYY HH:mm")
                                             (t/time-zone-for-id "Europe/Helsinki")))

(defn format-datetime-to-finnish-format [datetime]
  (f/unparse finnish-datetime-formatter datetime))

(defn finnish-datetime-from-long [timestamp-long]
  (format-datetime-to-finnish-format (c/from-long timestamp-long)))

(filters/add-filter! :datetime-format-with-dots-from-long
                     (fn [timestamp-long]
                       (finnish-datetime-from-long timestamp-long)))

(defn- make-email
  [email-data render-file-fn]
  (when (seq (:recipients email-data))
        (let [template-params (:template-params email-data)
              body (render-file-fn template-params)]
          {:from (:from email-data)
           :recipients (:recipients email-data)
           :subject (:subject email-data)
           :body body})))

(defn make-email-data
  [recipient subject template-params]
  {:from from-address
   :recipients [recipient]
   :subject subject
   :template-params template-params})


(defn- create-email [recipient locale trans-ns template-file & {:as params}]
   (let [lang                            (keyword locale)
         trans                           (partial get-translation lang)
         subject                         (str (trans :email/subject-prefix) ": " (trans (keyword (name trans-ns) (name :otsikko))))
         template-params                 (merge params
                                           (get-translation-ns lang trans-ns)
                                           {:lang              lang
                                            :information-email "recognition@oph.fi"
                                            })
         applicant-email-data            (make-email-data recipient subject template-params)
         render-file-fn                  (fn [template-params]
                                            (selmer/render-file template-file template-params))]
     (make-email
       applicant-email-data
       render-file-fn)))


(defn create-tutu-processing-email [recipient locale application-id]
  (create-email recipient
                locale
                :email-käsittely
                "templates/tutu_payment_processing.html"
                :application-id application-id))

(defn create-tutu-decision-email [recipient locale]
  (create-email recipient
                locale
                :email-päätös
                "templates/tutu_payment_decision.html"))

(defn create-payment-receipt
  [recipient locale payment-reference timestamp-millis total-amount items]
  (create-email recipient
                locale
                :kuitti
                "templates/payment_receipt.html"
                :payment-reference payment-reference
                :date-of-purchase timestamp-millis
                :total-amount total-amount
                :items items
                :lang locale))
