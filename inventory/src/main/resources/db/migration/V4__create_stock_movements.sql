CREATE TABLE stock_movements (
                                 id BIGSERIAL PRIMARY KEY,
                                 product_id BIGINT NOT NULL,
                                 location_id BIGINT NOT NULL,
                                 type VARCHAR(20) NOT NULL,
                                 quantity DECIMAL(38,2) NOT NULL,
                                 source VARCHAR(20) NOT NULL,
                                 reference_id VARCHAR(255),
                                 created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                 CONSTRAINT fk_movement_product FOREIGN KEY (product_id) REFERENCES products(id),
                                 CONSTRAINT fk_movement_location FOREIGN KEY (location_id) REFERENCES locations(id),
                                 CONSTRAINT chk_movement_type CHECK (type IN ('ENTRY', 'EXIT', 'ADJUSTMENT')),
                                 CONSTRAINT chk_movement_source CHECK (source IN ('MANUAL', 'CAMERA', 'IMPORT', 'SYSTEM'))
);