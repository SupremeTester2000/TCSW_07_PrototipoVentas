CREATE TABLE products (
    code VARCHAR(6) PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    price NUMERIC(10, 2) NOT NULL CHECK (price >= 0),
    stock INTEGER NOT NULL CHECK (stock >= 0)
);

CREATE TABLE sales (
    sale_id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    subtotal NUMERIC(12, 2) NOT NULL CHECK (subtotal >= 0),
    discount_amount NUMERIC(12, 2) NOT NULL DEFAULT 0 CHECK (discount_amount >= 0),
    tax_amount NUMERIC(12, 2) NOT NULL DEFAULT 0 CHECK (tax_amount >= 0),
    total NUMERIC(12, 2) NOT NULL CHECK (total >= 0)
);

CREATE TABLE sale_details (
    sale_id BIGINT NOT NULL,
    line_number SMALLINT NOT NULL,
    product_code VARCHAR(6) NOT NULL,
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    captured_price NUMERIC(10, 2) NOT NULL CHECK (captured_price >= 0),
    CONSTRAINT pk_sale_details PRIMARY KEY (sale_id, line_number),
    CONSTRAINT fk_sale_details_sale FOREIGN KEY (sale_id)
        REFERENCES sales (sale_id),
    CONSTRAINT fk_sale_details_product FOREIGN KEY (product_code)
        REFERENCES products (code)
);

CREATE INDEX idx_sale_details_product_code ON sale_details (product_code);