CREATE TABLE stocks (
                        id BIGSERIAL PRIMARY KEY,
                        product_id BIGINT NOT NULL,
                        location_id BIGINT NOT NULL,
                        quantity DECIMAL(38,2) NOT NULL,
                        version BIGINT,

                        CONSTRAINT fk_stock_product FOREIGN KEY (product_id) REFERENCES products(id),
                        CONSTRAINT fk_stock_location FOREIGN KEY (location_id) REFERENCES locations(id),
                        CONSTRAINT uk_stock_product_location UNIQUE (product_id, location_id)
);