INSERT INTO products (code, name, price, stock) VALUES
    ('P00001', 'Laptop', 1200.00, 10),
    ('P00002', 'Mouse', 250.00, 20),
    ('P00003', 'Teclado', 400.00, 15)
ON CONFLICT (code) DO NOTHING;
