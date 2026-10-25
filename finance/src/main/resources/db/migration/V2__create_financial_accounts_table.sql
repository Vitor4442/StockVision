CREATE TABLE financial_accounts (
    id UUID PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    type VARCHAR(20) NOT NULL,
    institution VARCHAR(120),
    account_number VARCHAR(80),
    currency VARCHAR(3) NOT NULL DEFAULT 'BRL',
    initial_balance NUMERIC(19, 2) NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    deleted_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT chk_financial_accounts_type CHECK (type IN ('BANK', 'CASH', 'WALLET')),
    CONSTRAINT chk_financial_accounts_status CHECK (status IN ('ACTIVE', 'INACTIVE')),
    CONSTRAINT chk_financial_accounts_currency CHECK (currency ~ '^[A-Z]{3}$')
);

CREATE INDEX idx_financial_accounts_type_status ON financial_accounts (type, status);
CREATE INDEX idx_financial_accounts_deleted_at ON financial_accounts (deleted_at);
