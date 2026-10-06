CREATE TABLE products (
                          id BIGSERIAL PRIMARY KEY,
                          name VARCHAR(100) NOT NULL,
                          sku VARCHAR(50) NOT NULL,
                          description VARCHAR(500),
                          active BOOLEAN NOT NULL DEFAULT true,
                          price DECIMAL(10,2) NOT NULL DEFAULT 0.00,
                          cost_price DECIMAL(10,2) NOT NULL DEFAULT 0.00,

                          CONSTRAINT uk_product_sku UNIQUE (sku),
                          CONSTRAINT chk_product_price CHECK (price >= 0),
                          CONSTRAINT chk_product_cost_price CHECK (cost_price >= 0)
);

CREATE INDEX idx_products_sku ON products(sku);