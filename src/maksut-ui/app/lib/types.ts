export type PaymentStatus = 'active' | 'paid' | 'overdue'

export type PaymentState = 'kasittelymaksamatta' | 'kasittelymaksettu' | 'paatosmaksamatta' | 'paatosmaksettu'

export type Origin = 'tutu' | 'astu' | 'kkhakemusmaksu'

export type Locale = 'fi' | 'en' | 'sv'

export type LocalizedString = {
  fi?: string;
  en?: string;
  sv?: string;
}

export type Metadata = {
  form_name?: LocalizedString;
  order_id_prefix?: string
  haku_name?: LocalizedString
  alkamisvuosi?: number
  alkamiskausi?: string
}

export type Lasku = {
  order_id: string;
  first_name: string;
  last_name: string;
  amount: string;
  due_date: string;
  status: PaymentStatus;
  secret: string;
  paid_at: string;
  origin: Origin;
  reference: string;
  metadata?: Metadata;
  vat?: string;
}


// (s/defschema PaytrailCallbackRequest
// {:ORDER_NUMBER s/Str
// :PAYMENT_ID s/Str
// :AMOUNT s/Str
// :TIMESTAMP s/Int
// :STATUS s/Str
// :RETURN_AUTHCODE s/Str})
//
// (s/defschema TutuPassthruCallbackRequest
// {
// :tutusecret s/Str
// :tutulocale s/Str})
//
// (s/defschema TutuPaytrailCallbackRequest
// (st/merge
// PaytrailCallbackRequest
// TutuPassthruCallbackRequest))
//

// (s/defschema PaymentStatus
// (s/enum
// :active
//   :paid
//   :overdue))
//
// (s/defschema LaskuRefList
// {:keys [s/Str]})
//
// (s/defschema TutuLaskuCreate
// {:application-key s/Str
// :first-name s/Str
// :last-name s/Str
// :email s/Str
// :amount s/Str
// (s/optional-key :due-date) (s/maybe s/Str)
// :index (s/constrained s/Int #(<= 1 % 2) 'valid-tutu-maksu-index)
// })
//
// (s/defschema LaskuCreate
// {:order-id s/Str
// :first-name s/Str
// :last-name s/Str
// :email s/Str
// :amount s/Str
// (s/optional-key :due-date) (s/maybe s/Str) ;If not defined, then due-days used
//   :due-days (s/constrained s/Int #(> % 0) 'positive-due-days)
// :origin s/Str
// :reference s/Str})
//
// (s/defschema LaskuStatus
// {:order-id s/Str
// :reference s/Str
// :status PaymentStatus})
//
// (s/defschema ErrorResponse
// {:error s/Bool
// :type s/Keyword
//   :code s/Keyword
// :message s/Str})
//
// (s/defschema Lasku
// {:order_id s/Str
// :first_name s/Str
// :last_name s/Str
// :amount s/Str ; java.math.BigDecimal - Does not port to CLJS
//   :due_date s/Str ;java.time.LocalDate - Does not port to CLJS
//   :status PaymentStatus
// (s/optional-key :secret) s/Str
// (s/optional-key :paid_at) s/Str  ;java.time.LocalDate - Does not port to CLJS
// })
//
// (s/defschema Laskut
//   [Lasku])
//
