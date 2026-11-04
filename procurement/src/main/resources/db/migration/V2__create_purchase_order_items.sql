CREATE SEQUENCE purchase_order_items_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE purchase_order_items (
    id BIGINT NOT NULL DEFAULT nextval('purchase_order_items_seq'),
    purchase_order_id BIGINT,
    product_id BIGINT NOT NULL,
    quantity NUMERIC(38, 2) NOT NULL,
    CONSTRAINT pk_purchase_order_items PRIMARY KEY (id),
    CONSTRAINT fk_purchase_order_items_purchase_order
        FOREIGN KEY (purchase_order_id) REFERENCES purchase_orders (id)
);
