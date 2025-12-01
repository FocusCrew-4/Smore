INSERT INTO p_member (role, email, password, nickname, auction_cancel_count, status, registration_status, created_at, updated_at)
VALUES ('ADMIN', 'admin@example.com', 'hashed-admin-password', 'Admin', 0, 'ACTIVE', 'REGISTERED', NOW(), NOW())
ON CONFLICT (email) DO NOTHING;

INSERT INTO p_member (role, email, password, nickname, auction_cancel_count, status, registration_status, created_at, updated_at)
VALUES ('SELLER', 'seller@example.com', 'hashed-seller-password', 'Seller', 0, 'ACTIVE', 'REGISTERED', NOW(), NOW())
ON CONFLICT (email) DO NOTHING;

INSERT INTO p_member (role, email, password, nickname, auction_cancel_count, status, registration_status, created_at, updated_at)
VALUES ('CONSUMER', 'consumer@example.com', 'hashed-consumer-password', 'Consumer', 0, 'ACTIVE', 'REGISTERED', NOW(), NOW())
ON CONFLICT (email) DO NOTHING;
