INSERT INTO products (code, name, price, stock)
VALUES ('P00001', 'Producto de ejemplo', 10.99, 100)
ON CONFLICT (code) DO NOTHING;