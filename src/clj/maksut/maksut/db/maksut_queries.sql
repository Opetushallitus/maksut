
-- :name insert-lasku-returning! :! :1
INSERT INTO invoices (
    order_id,
    first_name,
    last_name,
    email,
    amount,
    origin,
    reference,
    due_date
)
VALUES (
    :order-id,
    :first-name,
    :last-name,
    :email,
    :amount,
    :origin,
    :reference,
    CURRENT_DATE + INTERVAL '1 day' * :due-days
)
RETURNING * ;

-- :name update-lasku! :! :n
UPDATE invoices
SET
    first_name = :first-name,
    last_name = :last-name,
    email = :email,
    amount = :amount
WHERE order_id = :order-id AND CURRENT_DATE <= due_date;

-- :name get-lasku-locked :? :1
SELECT *
FROM invoices
WHERE order_id = :order-id
FOR NO KEY UPDATE;

-- :name insert-payment! :! :n
INSERT INTO payments (
    fk_invoice,
    payment_id,
    amount,
    paid_at
)
VALUES (
    :invoice-id,
    :payment-id,
    :amount,
    to_timestamp(:timestamp)::timestamp without time zone
);

-- :name select-payment :? :1
SELECT *
FROM payments
WHERE fk_invoice = :invoice-id AND payment_id = :payment-id;

-- :name get-lasku-by-order-id :? :1
SELECT *
FROM all_invoices
WHERE order_id = :order-id;

-- :name all-linked-laskut-by-secret :? :*
SELECT ai.* FROM all_invoices ai
JOIN (
	SELECT origin, reference FROM invoices WHERE id = (SELECT fk_invoice FROM secrets WHERE secret = :secret LIMIT 1)
) sharing_refs ON (ai.origin = sharing_refs.origin AND ai.reference = sharing_refs.reference AND ai.reference IS NOT NULL);
