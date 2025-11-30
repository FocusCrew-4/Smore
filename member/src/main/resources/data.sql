-- Seed members by role/status
INSERT INTO p_member (id, role, email, password, nickname, auction_cancel_count, status, created_at, updated_at, deleted_at, deleted_by)
VALUES
    (1, 'ADMIN', 'admin@example.com', 'hashed-admin-password', 'admin', 0, 'ACTIVE', NOW(), NOW(), NULL, NULL),
    (2, 'SELLER', 'seller_pending@example.com', 'hashed-seller-password', 'seller_pending', 0, 'ACTIVE', NOW(), NOW(), NULL, NULL),
    (3, 'SELLER', 'seller_active@example.com', 'hashed-seller-password', 'seller_active', 0, 'ACTIVE', NOW(), NOW(), NULL, NULL),
    (4, 'SELLER', 'seller_inactive@example.com', 'hashed-seller-password', 'seller_inactive', 0, 'INACTIVE', NOW(), NOW(), NULL, NULL),
    (5, 'SELLER', 'seller_deleted@example.com', 'hashed-seller-password', 'seller_deleted', 1, 'DELETED', NOW(), NOW(), NOW(), 1),
    (6, 'SELLER', 'seller_banned@example.com', 'hashed-seller-password', 'seller_banned', 2, 'BANNED', NOW(), NOW(), NOW(), 1),
    (7, 'CONSUMER', 'consumer_active@example.com', 'hashed-consumer-password', 'consumer_active', 0, 'ACTIVE', NOW(), NOW(), NULL, NULL),
    (8, 'CONSUMER', 'consumer_inactive@example.com', 'hashed-consumer-password', 'consumer_inactive', 0, 'INACTIVE', NOW(), NOW(), NULL, NULL)
ON CONFLICT DO NOTHING;

-- Seed sellers by status (linked to seller members above)
INSERT INTO p_seller (id, member_id, account_num, status, created_at, updated_at, deleted_at, deleted_by)
VALUES
    ('00000000-0000-0000-0000-000000000001', 2, '111-11-111111', 'PENDING', NOW(), NOW(), NULL, NULL),
    ('00000000-0000-0000-0000-000000000002', 3, '222-22-222222', 'ACTIVE', NOW(), NOW(), NULL, NULL),
    ('00000000-0000-0000-0000-000000000003', 4, '333-33-333333', 'INACTIVE', NOW(), NOW(), NULL, NULL),
    ('00000000-0000-0000-0000-000000000004', 5, '444-44-444444', 'DELETED', NOW(), NOW(), NOW(), 1),
    ('00000000-0000-0000-0000-000000000005', 6, '555-55-555555', 'BANNED', NOW(), NOW(), NOW(), 1)
ON CONFLICT DO NOTHING;
