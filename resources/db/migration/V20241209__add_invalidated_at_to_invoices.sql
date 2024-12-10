ALTER TABLE invoices ADD COLUMN invalidated_at TIMESTAMP;

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
    i.invalidated_at
FROM invoices i
         LEFT OUTER JOIN latest_secrets s on (i.id = s.id)
         LEFT OUTER JOIN latest_payments p on (i.id = p.id);
