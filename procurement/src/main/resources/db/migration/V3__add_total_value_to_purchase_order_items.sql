ALTER TABLE purchase_order_items
    ADD COLUMN total_value NUMERIC(38, 2) NOT NULL DEFAULT 0;

ALTER TABLE purchase_order_items
    ALTER COLUMN total_value DROP DEFAULT;
