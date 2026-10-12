INSERT INTO provider
    (business_name, service_type, experience, phone, email, address, role)
SELECT
    'ABC Electricals', 'Electrician', '5 years',
    '9876543210', 'abc@gmail.com', 'Hyderabad', 'Provider'
WHERE NOT EXISTS (
    SELECT 1 FROM provider WHERE email = 'abc@gmail.com'
);

INSERT INTO provider
    (business_name, service_type, experience, phone, email, address, role)
SELECT
    'City Plumbing Services', 'Plumber', '4 years',
    '9876543211', 'cityplumbing@example.com', 'Hyderabad', 'Provider'
WHERE NOT EXISTS (
    SELECT 1 FROM provider WHERE email = 'cityplumbing@example.com'
);

INSERT INTO provider
    (business_name, service_type, experience, phone, email, address, role)
SELECT
    'Hyderabad Home Cleaning', 'Home Cleaning', '3 years',
    '9876543212', 'homecleaning@example.com', 'Hyderabad', 'Provider'
WHERE NOT EXISTS (
    SELECT 1 FROM provider WHERE email = 'homecleaning@example.com'
);
