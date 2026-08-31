ALTER TABLE invoices ADD COLUMN terms_agreed_at TIMESTAMP;

CREATE OR REPLACE VIEW all_invoices
AS
SELECT
    i.id,
    i.order_id,
    i.first_name,
    i.last_name,
    i.email,
    i.amount,
    i.origin,
    i.reference,
    i.due_date,
    i.created_at,
    s.secret,
    CASE
        WHEN p.paid_at IS NOT NULL THEN 'paid'
        WHEN i.invalidated_at IS NOT NULL THEN 'invalidated'
        WHEN i.due_date < CURRENT_DATE THEN 'overdue'
        ELSE 'active'
        END AS status,
    p.paid_at,
    i.metadata,
    i.vat,
    i.invalidated_at,
    i.terms_agreed_at
FROM invoices i
         LEFT OUTER JOIN latest_secrets s on (i.id = s.id)
         LEFT OUTER JOIN latest_payments p on (i.id = p.id);

ALTER TABLE invoices_history ADD COLUMN IF NOT EXISTS terms_agreed_at TIMESTAMP;

CREATE OR REPLACE FUNCTION update_invoices_history() RETURNS TRIGGER AS
$$
BEGIN
    INSERT INTO invoices_history (
        id,
        order_id,
        first_name,
        last_name,
        email,
        amount,
        origin,
        reference,
        due_date,
        created_at,
        metadata,
        vat,
        invalidated_at,
        terms_agreed_at
    ) VALUES (
                 old.id,
                 old.order_id,
                 old.first_name,
                 old.last_name,
                 old.email,
                 old.amount,
                 old.origin,
                 old.reference,
                 old.due_date,
                 old.created_at,
                 old.metadata,
                 old.vat,
                 old.invalidated_at,
                 old.terms_agreed_at
             );
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;
