CREATE TABLE payment_attempts (
    fk_invoice INTEGER REFERENCES invoices (id) NOT NULL,
    created_at TIMESTAMP DEFAULT now()          NOT NULL,
    PRIMARY KEY (fk_invoice, created_at)
);
