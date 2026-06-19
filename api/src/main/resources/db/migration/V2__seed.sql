INSERT INTO clients (name, document_id, document_type, plan_type, balance, monthly_limit, monthly_usage, active, admin)
VALUES
    ('Maria Prepaid',     '52998224725',    'CPF',  'PREPAID',  10.00,  0.00,  0.00, TRUE, FALSE),
    ('Acme Postpaid LTDA','11222333000181', 'CNPJ', 'POSTPAID',  0.00, 50.00,  0.00, TRUE, FALSE),
    ('Admin User',        '11144477735',    'CPF',  'PREPAID', 100.00,  0.00,  0.00, TRUE, TRUE)
ON CONFLICT (document_id) DO NOTHING;
