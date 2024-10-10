(ns maksut.api-schemas
  (:require [schema.core :as s]
            [schema-tools.core :as st]))

(s/defschema Locale
  (s/enum
    "fi"
    "sv"
    "en"))

(s/defschema Origin
  (s/enum
    "tutu"
    "astu"))

(s/defschema LocalizationEntity
  {:id       s/Int
   :category s/Str
   :key      s/Str
   :locale   Locale
   :value    s/Str
   s/Any     s/Any})

(s/defschema LocalizedString
  {(s/optional-key :fi) s/Str
   (s/optional-key :sv) s/Str
   (s/optional-key :en) s/Str})

(s/defschema Metadata
  {(s/optional-key :form_name) LocalizedString})

(s/defschema MetadataCreate
  {(s/optional-key :form-name) LocalizedString})

;Paytrail palauttamat kentät (konfiguroitavissa PARAMS-OUT kentässä)
(s/defschema PaytrailCallbackRequest
  {:ORDER_NUMBER s/Str
   :PAYMENT_ID s/Str
   :AMOUNT s/Str
   :TIMESTAMP s/Int
   :STATUS s/Str
   :RETURN_AUTHCODE s/Str})

;Läpilasketut kentät
(s/defschema PassthruCallbackRequest
  {:secret s/Str
   :locale s/Str})

(s/defschema EnrichedPaytrailCallbackRequest
  (st/merge
    PaytrailCallbackRequest
    PassthruCallbackRequest))

(s/defschema PaymentStatus
  (s/enum
    :active
    :paid
    :overdue))

(s/defschema LaskuRefList
  {:keys [s/Str]})

(s/defschema TutuLaskuCreate
  {:application-key s/Str
   :first-name s/Str
   :last-name s/Str
   :email s/Str
   :amount s/Str
   (s/optional-key :due-date) (s/maybe s/Str)
   :index (s/constrained s/Int #(<= 1 % 2) 'valid-tutu-maksu-index)})

(s/defschema LaskuCreate
  {(s/optional-key :order-id) s/Str
   :first-name s/Str
   :last-name s/Str
   :email s/Str
   :amount s/Str
   (s/optional-key :due-date) (s/maybe s/Str) ;If not defined, then due-days used
   :due-days (s/constrained s/Int #(> % 0) 'positive-due-days)
   :origin Origin
   :reference s/Str
   (s/optional-key :index) (s/constrained s/Int #(<= 1 % 2) 'valid-tutu-maksu-index)
   (s/optional-key :metadata) MetadataCreate})

(s/defschema LaskuStatus
  {:order_id s/Str
   :reference s/Str
   :status PaymentStatus
   :origin Origin})

(s/defschema ErrorResponse
  {:error s/Bool
   :type s/Keyword
   :code s/Keyword
   :message s/Str})

(s/defschema Lasku
  {:order_id s/Str
   :first_name s/Str
   :last_name s/Str
   :amount s/Str ; java.math.BigDecimal - Does not port to CLJS
   :due_date s/Str ;java.time.LocalDate - Does not port to CLJS
   :status PaymentStatus
   (s/optional-key :secret) s/Str
   (s/optional-key :paid_at) s/Str  ;java.time.LocalDate - Does not port to CLJS
   :origin Origin
   :reference s/Str
   (s/optional-key :metadata) Metadata})

(s/defschema Laskut
  [Lasku])

(s/defschema LaskuStatusList
  [LaskuStatus])
