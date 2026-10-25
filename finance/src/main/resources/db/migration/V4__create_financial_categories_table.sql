CREATE TABLE financial_categories (
    id UUID PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    description VARCHAR(500),
    type VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    deleted_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT chk_financial_categories_type CHECK (type IN ('PAYABLE', 'RECEIVABLE')),
    CONSTRAINT chk_financial_categories_status CHECK (status IN ('ACTIVE', 'INACTIVE'))
);

CREATE UNIQUE INDEX uq_financial_categories_active_name_type
    ON financial_categories (lower(name), type)
    WHERE deleted_at IS NULL;
CREATE INDEX idx_financial_categories_type_status
    ON financial_categories (type, status);

ALTER TABLE financial_records
    ADD CONSTRAINT fk_financial_records_category
    FOREIGN KEY (category_id) REFERENCES financial_categories(id) NOT VALID;
