ALTER TABLE invoices_history ADD COLUMN metadata JSONB;
ALTER TABLE invoices_history ADD COLUMN vat NUMERIC;
ALTER TABLE invoices_history ADD COLUMN invalidated_at TIMESTAMP;

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
        invalidated_at
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
        old.invalidated_at
    );
RETURN NULL;
END;
$$ LANGUAGE plpgsql;
