ALTER TABLE purchase_order_items
    ADD COLUMN unit_value NUMERIC(38, 2) NOT NULL DEFAULT 0;

ALTER TABLE purchase_order_items
    ALTER COLUMN unit_value DROP DEFAULT;
