---
--- Tables
---

CREATE TABLE invoices (
    id                      SERIAL PRIMARY KEY,
    order_id				TEXT NOT NULL UNIQUE,                 --Paytrailiin lähetettävä orderId, sisältäen esim TTU prefixin ja suffixin
    first_name				TEXT NOT NULL,
    last_name				TEXT NOT NULL,
    email					TEXT NOT NULL,
	amount                  NUMERIC NOT NULL,
	origin					TEXT NOT NULL,                        --Mikä palvelu loi tämän laskun
	reference               TEXT,                                 --Esim application-key oid
    due_date         		DATE NOT NULL,
    created_at         		TIMESTAMP DEFAULT now() NOT NULL
);

CREATE TABLE invoices_history (
    id                      INTEGER,
    order_id				TEXT NOT NULL,
    first_name				TEXT NOT NULL,
    last_name				TEXT NOT NULL,
    email					TEXT NOT NULL,
	amount                  NUMERIC NOT NULL,
	origin					TEXT NOT NULL,
	reference               TEXT,
    due_date         		DATE NOT NULL,
    created_at         		TIMESTAMP NOT NULL
);

CREATE TABLE payments (
    fk_invoice				INTEGER REFERENCES invoices(id) NOT NULL,
	payment_id              TEXT NOT NULL,
	amount                  NUMERIC NOT NULL,
	paid_at					TIMESTAMP,
	created_at         		TIMESTAMP DEFAULT now() NOT NULL,
	UNIQUE (fk_invoice, payment_id)
);

CREATE TABLE secrets (
	fk_invoice              INTEGER REFERENCES invoices(id) NOT NULL,
	secret                  TEXT NOT NULL,
	created_at         		TIMESTAMP DEFAULT now() NOT NULL
);


-- Required by ring.middleware
create table sessions (
    key  varchar(40) primary key,
    data jsonb,
	created_at timestamp default now()
);


--- Prefix secrets with order_id to force them unique even if random would generate two identical

---
--- Views
---

CREATE VIEW latest_secrets
AS
SELECT DISTINCT ON (i.id) id, s.secret
FROM invoices i, secrets s
WHERE i.id = s.fk_invoice
ORDER BY id, s.created_at DESC;

CREATE VIEW latest_payments
AS
SELECT DISTINCT ON (i.id) id, p.amount, p.paid_at, p.payment_id
FROM invoices i, payments p
WHERE i.id = p.fk_invoice
ORDER BY id, p.paid_at ASC;

CREATE VIEW all_invoices
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
        WHEN i.due_date < CURRENT_DATE THEN 'overdue'
        ELSE 'active'
    END AS status,
    p.paid_at
FROM invoices i
LEFT OUTER JOIN latest_secrets s on (i.id = s.id)
LEFT OUTER JOIN latest_payments p on (i.id = p.id);

---
--- Functions / Triggers
---

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
        created_at
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
        old.created_at
    );
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER invoices_history
  AFTER UPDATE ON invoices
  FOR EACH ROW
EXECUTE PROCEDURE update_invoices_history();
