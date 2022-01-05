(ns maksut.api-schemas
  (:require [schema.core :as s]
            [schema-tools.core :as st]
            [maksut.common-schemas :as c]))

(s/defschema Locale
  (s/enum
    "fi"
    "sv"
    "en"))

(s/defschema LocalizationEntity
  {:id       s/Int
   :category s/Str
   :key      s/Str
   :locale   (s/enum "fi" "sv" "en")
   :value    s/Str
   s/Any     s/Any})

;Paytrail palauttamat kentät (konfiguroitavissa PARAMS-OUT kentässä)
(s/defschema PaytrailCallbackRequest
  {:ORDER_NUMBER s/Str
   :PAYMENT_ID s/Str
   :AMOUNT s/Str
   :TIMESTAMP s/Int
   :STATUS s/Str
   :RETURN_AUTHCODE s/Str})

;Läpilasketut kentät (Tutu-specificc, näitä ei välttämättä tarvita geneeriselle maksulle)
(s/defschema TutuPassthruCallbackRequest
  {
    :tutusecret s/Str
    :tutulocale s/Str})

(s/defschema TutuPaytrailCallbackRequest
  (st/merge
    PaytrailCallbackRequest
    TutuPassthruCallbackRequest))

(s/defschema PaymentStatus
  (s/enum
    :active
    :paid
    :overdue))

(s/defschema LaskuRefList
  {:keys [s/Str]})

(s/defschema TutuLaskuCreate
  {:application-key s/Str ;TODO validate proper oid-syntax
   :first-name s/Str
   :last-name s/Str
   :email s/Str
   :amount s/Str
   (s/optional-key :due_date) (s/maybe s/Str)
   :index (s/constrained s/Int #(<= 1 % 2) 'valid-tutu-maksu-index)
   })

(s/defschema LaskuCreate
  {:order-id s/Str
   :first-name s/Str
   :last-name s/Str
   :email s/Str
   :amount s/Str
   :due-days (s/constrained s/Int #(> % 0) 'positive-due-days)
   :origin s/Str
   :reference s/Str})

(s/defschema LaskuStatus
  {:order-id s/Str
   :reference s/Str
   :status PaymentStatus})

;(s/defschema TutuLaskuList
;  {:application-key s/Str ;TODO validate proper oid-syntax
;   (s/optional-key :index) (s/constrained s/Int #(<= 1 % 2) 'valid-tutu-maksu-index)
;   })

;(s/defschema MaksutResponse
;  {:secret s/Str
;   :status PaymentStatus
;   :email s/Str})

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
   })

(s/defschema Laskut
  [Lasku])

