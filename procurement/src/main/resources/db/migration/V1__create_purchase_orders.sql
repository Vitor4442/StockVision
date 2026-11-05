CREATE SEQUENCE purchase_orders_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE purchase_orders (
    id BIGINT NOT NULL DEFAULT nextval('purchase_orders_seq'),
    order_number VARCHAR(30) NOT NULL,
    status VARCHAR(30) NOT NULL,
    subtotal NUMERIC(15, 2) NOT NULL,
    discount NUMERIC(15, 2),
    shipping_cost NUMERIC(15, 2),
    total_amount NUMERIC(15, 2) NOT NULL,
    notes OID,
    expected_delivery_at TIMESTAMP,
    ordered_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    CONSTRAINT pk_purchase_orders PRIMARY KEY (id),
    CONSTRAINT uk_purchase_orders_order_number UNIQUE (order_number)
);
