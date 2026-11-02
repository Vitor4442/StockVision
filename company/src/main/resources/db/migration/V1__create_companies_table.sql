CREATE TABLE companies (
    id BIGSERIAL PRIMARY KEY,
    singleton_key SMALLINT NOT NULL DEFAULT 1 UNIQUE CHECK (singleton_key = 1),
    legal_name VARCHAR(160) NOT NULL,
    trade_name VARCHAR(160),
    cnpj VARCHAR(14) NOT NULL UNIQUE,
    email VARCHAR(160),
    phone VARCHAR(30),
    address VARCHAR(255),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
