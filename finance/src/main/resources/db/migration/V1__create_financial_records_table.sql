CREATE TABLE financial_records (
    id UUID PRIMARY KEY,
    type VARCHAR(20) NOT NULL,
    description VARCHAR(500) NOT NULL,
    amount NUMERIC(19, 2) NOT NULL,
    issue_date DATE,
    due_date DATE,
    status VARCHAR(20) NOT NULL,
    category_id UUID,
    counterparty_id UUID,
    source_type VARCHAR(20),
    source_id UUID,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    deleted_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT chk_financial_records_amount_positive CHECK (amount > 0),
    CONSTRAINT chk_financial_records_type CHECK (type IN ('PAYABLE', 'RECEIVABLE')),
    CONSTRAINT chk_financial_records_status CHECK (status IN ('PENDING', 'PAID', 'CANCELLED')),
    CONSTRAINT chk_financial_records_source_type CHECK (source_type IS NULL OR source_type IN ('SALE', 'PURCHASE', 'MANUAL'))
);

CREATE INDEX idx_financial_records_type_status ON financial_records (type, status);
CREATE INDEX idx_financial_records_due_date ON financial_records (due_date);
