(ns maksut.email.tutu-payment-confirmation
  (:require ;[ataru.background-job.email-job :as email-job]
            ;[ataru.background-job.job :as job]
            ;[maksut.config :as config]
            [maksut.util.translation :refer [get-translation get-translation-ns]]
            [markdown.core :as md]
            [selmer.parser :as selmer]
            [taoensso.timbre :as log])
  (:import [org.owasp.html HtmlPolicyBuilder ElementPolicy]))

(def from-address "no-reply@opintopolku.fi")

;(defn ->safe-html
;  [content]
;  (.sanitize html-policy (md/md-to-html-string content)))

;(defn- modify-link [secret]
;  (-> config
;      (get-in [:public-config :applicant :service_url])
;      (str "/hakemus?modify=" secret)))


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


(defn create-processing-email [recipient locale application-id]
  (create-email recipient
                locale
                :email-käsittely
                "templates/tutu_payment_processing.html"
                :application-id application-id))

(defn create-decision-email [recipient locale]
  (create-email recipient
                locale
                :email-päätös
                "templates/tutu_payment_decision.html"))


;(defn start-email-job [job-runner email]
;  (let [job-id (jdbc/with-db-transaction [connection {:datasource (db/get-datasource :db)}]
;                 (job/start-job job-runner
;                                connection
;                                (:type email-job/job-definition)
;                                email))]
;    (log/info "Started application confirmation email job (to viestintäpalvelu) with job id" job-id ":")
;    (log/info email)))
;
;(defn start-email-submit-confirmation-job
;  [koodisto-cache tarjonta-service organization-service ohjausparametrit-service job-runner application-id payment-url]
;  (dorun
;    (for [email (create-submit-email koodisto-cache tarjonta-service
;                  organization-service
;                  ohjausparametrit-service
;                  application-id
;                  payment-url
;                  true)]
;      (start-email-job job-runner email))))


