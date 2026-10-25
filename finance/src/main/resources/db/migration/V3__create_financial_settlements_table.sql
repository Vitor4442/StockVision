CREATE TABLE financial_settlements (
    id UUID PRIMARY KEY,
    financial_entry_id UUID NOT NULL REFERENCES financial_records(id),
    amount NUMERIC(19, 2) NOT NULL,
    paid_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    financial_account_id UUID NOT NULL REFERENCES financial_accounts(id),
    method VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'CONFIRMED',
    CONSTRAINT chk_financial_settlements_amount_positive CHECK (amount > 0),
    CONSTRAINT chk_financial_settlements_method CHECK
        (method IN ('PIX', 'CASH', 'BANK_TRANSFER', 'CREDIT_CARD', 'DEBIT_CARD', 'CHECK', 'OTHER')),
    CONSTRAINT chk_financial_settlements_status CHECK (status IN ('CONFIRMED', 'CANCELLED'))
);

CREATE INDEX idx_financial_settlements_entry_paid_at
    ON financial_settlements (financial_entry_id, paid_at DESC);
CREATE INDEX idx_financial_settlements_account ON financial_settlements (financial_account_id);
