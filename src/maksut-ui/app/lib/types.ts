export type PaymentStatus = 'active' | 'paid' | 'overdue';

export type PaymentState =
  | 'kasittelymaksamatta'
  | 'kasittelymaksettu'
  | 'paatosmaksamatta'
  | 'paatosmaksettu';

export type Origin = 'tutu' | 'astu' | 'kkhakemusmaksu';

export type Locale = 'fi' | 'en' | 'sv';

export interface LocalizedString {
  fi?: string;
  en?: string;
  sv?: string;
}

export interface Metadata {
  form_name?: LocalizedString;
  order_id_prefix?: string
  haku_name?: LocalizedString
  alkamisvuosi?: number
  alkamiskausi?: string
}

export interface Lasku {
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
