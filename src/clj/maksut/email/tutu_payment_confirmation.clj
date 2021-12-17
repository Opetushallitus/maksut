(ns maksut.email.tutu-payment-confirmation
  (:require ;[ataru.background-job.email-job :as email-job]
            ;[ataru.background-job.job :as job]
            ;[maksut.config :as config]
            [markdown.core :as md]
            [selmer.parser :as selmer]
            [taoensso.timbre :as log])
  (:import [org.owasp.html HtmlPolicyBuilder ElementPolicy]))

;(def languages #{:fi :sv :en})
;(def languages-map {:fi nil :sv nil :en nil})

(def from-address "no-reply@opintopolku.fi")

(def decision-email-subjects
  {:fi "Opintopolku: Päätösmaksusi on vastaanotettu"
   :sv "Studieinfo: Päätösmaksusi on vastaanotettu"
   :en "Studyinfo: Päätösmaksusi on vastaanotettu"})

(def processing-email-subjects
  {:fi "Opintopolku: Käsittelymaksusi on vastaanotettu"
   :sv "Studieinfo: Käsittelymaksusi on vastaanotettu"
   :en "Studyinfo: Käsittelymaksusi on vastaanotettu"})


;(def email-default-texts
;  {:email-submit-confirmation-template
;   {:submit-email-subjects      {:fi "Opintopolku: hakemuksesi on vastaanotettu"
;                                 :sv "Studieinfo: Din ansökan har mottagits"
;                                 :en "Studyinfo: Your application has been received"}
;    :with-application-period    {:fi "Voit katsella ja muokata hakemustasi hakuaikana yllä olevan linkin kautta. Älä jaa linkkiä ulkopuolisille. Jos käytät yhteiskäyttöistä tietokonetta, muista kirjautua ulos sähköpostiohjelmasta.\n\nJos sinulla on verkkopankkitunnukset, mobiilivarmenne tai sähköinen henkilökortti, voit vaihtoehtoisesti kirjautua sisään [Opintopolku.fi](https://www.opintopolku.fi):ssä, ja tehdä muutoksia hakemukseesi Oma Opintopolku -palvelussa hakuaikana. Oma Opintopolku -palvelussa voit lisäksi nähdä valintojen tulokset ja ottaa opiskelupaikan vastaan.\n"
;                                 :sv "Om du vill ändra din ansökan, kan du göra ändringar via länken ovan under ansökningstiden. Dela inte länken vidare till utomstående. Kom ihåg att logga ut från e-postprogrammet om du använder en offentlig dator.\n\nOm du har nätbankskoder, mobilcertifikat eller ett elektroniskt ID-kort, kan du alternativt logga in i [Studieinfo.fi](https://www.studieinfo.fi) och under ansökningstiden göra ändringarna i tjänsten Min Studieinfo. I tjänsten kan du också se ditt antagningsresultat och ta emot studieplatsen.\n"
;                                 :en "If you wish to edit your application, you can use the link above and make the changes within the application period. Do not share the link with others. If you are using a public or shared computer, remember to log out of the email application.\n\nIf you have Finnish online banking credentials, an electronic ID-card or mobile certificate, you can also log in at [Studyinfo.fi](https://www.studyinfo.fi) and make the changes in the My Studyinfo -service within the application period. In addition to making changes to your application, if you have access to the My Studyinfo -service you can also view the admission results and confirm the study place.\n"}
;    :without-application-period {:fi "Voit katsella ja muokata hakemustasi yllä olevan linkin kautta. Älä jaa linkkiä ulkopuolisille. Jos käytät yhteiskäyttöistä tietokonetta, muista kirjautua ulos sähköpostiohjelmasta.\n\nJos sinulla on verkkopankkitunnukset, mobiilivarmenne tai sähköinen henkilökortti, voit vaihtoehtoisesti kirjautua sisään [Opintopolku.fi](https://www.opintopolku.fi):ssä, ja tehdä muutoksia hakemukseesi Oma Opintopolku -palvelussa hakuaikana. Oma Opintopolku -palvelussa voit lisäksi nähdä valintojen tulokset ja ottaa opiskelupaikan vastaan.\n"
;                                 :sv "Om du vill ändra din ansökan, kan du göra ändringar via länken ovan. Dela inte länken vidare till utomstående. Kom ihåg att logga ut från e-postprogrammet om du använder en offentlig dator.\n\nOm du har nätbankskoder, mobilcertifikat eller ett elektroniskt ID-kort, kan du alternativt logga in i [Studieinfo.fi](https://www.studieinfo.fi) och under ansökningstiden göra ändringarna i tjänsten Min Studieinfo. I tjänsten kan du också, se antagningsresultaten och ta emot studieplatsen.\n"
;                                 :en "If you wish to edit your application, you can use the link above and make the changes within the application period. Do not share the link with others. If you are using a public or shared computer, remember to log out of the email application.\n\nIf you have Finnish online banking credentials, an electronic\nID-card or mobile certificate, you can also log in\nat [Studyinfo.fi](https://www.studyinfo.fi) and make the\nchanges in the My Studyinfo -service within the application period. In addition to making changes to your application, if you have access to the My Studyinfo -service you can also view the admission results and confirm the study place.\n"}
;    :signature                  {:fi "Älä vastaa tähän viestiin - viesti on lähetetty automaattisesti.\n\nYstävällisin terveisin <br/>\nOpintopolku\n"
;                                 :sv "Svara inte på detta meddelande, det har skickats automatiskt.\n\nMed vänliga hälsningar, <br/>\nStudieinfo\n"
;                                 :en "This is an automatically generated email, please do not reply.\n\nBest regards, <br/>\nStudyinfo\n"}}})

;(defn- ->string-array
;  [& elements]
;  (into-array String elements))
;
;(def add-style-to-links
;  (proxy [ElementPolicy] []
;    (apply [element-name attrs]
;      (doto attrs
;        (.add "target")
;        (.add "_blank")
;        (.add "style")
;        (.add "color: #0093C4;"))
;      element-name)))
;
;(def html-policy
;  (as-> (HtmlPolicyBuilder.) hpb
;        (.allowElements hpb (->string-array "p" "span" "div" "h1" "h2" "h3" "h4" "h5" "ul" "ol" "li" "br" "strong" "em"))
;        (.allowElements hpb add-style-to-links (->string-array "a"))
;        (.allowUrlProtocols hpb (->string-array "http" "https"))
;        (.onElements (.allowAttributes hpb (->string-array "href" "target")) (->string-array "a"))
;        (.toFactory hpb)))
;
;(defn ->safe-html
;  [content]
;  (.sanitize html-policy (md/md-to-html-string content)))

(defn- processing-template-filename
  [lang]
  (str "templates/tutu_payment_processing_" (name lang) ".html"))

(defn- decision-template-filename
  [lang]
  (str "templates/tutu_payment_decision_" (name lang) ".html"))

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


(defn- create-email [recipient subject-map template-name & {:as params}]
   (let [lang                            :fi
         subject                         (or (get subject-map lang) "Opintopolku: Maksu vastaanotettu")
         template-params                 (merge params
                                           {:lang              lang
                                            :information-url   "https://www.oph.fi/fi/palvelut/tutkintojen-tunnustaminen"
                                            :information-email "recognition@oph.fi"
                                           })
         applicant-email-data            (make-email-data recipient subject template-params)
         render-file-fn                  (fn [template-params]
                                            (selmer/render-file (template-name lang) template-params))]
     (make-email
       applicant-email-data
       render-file-fn)))


(defn create-processing-email [recipient application-id]
  (create-email recipient
                processing-email-subjects
                processing-template-filename
                :application-id application-id))

(defn create-decision-email [recipient]
  (create-email recipient
                decision-email-subjects
                decision-template-filename))


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


