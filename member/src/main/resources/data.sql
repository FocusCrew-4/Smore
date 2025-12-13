-- Seed members by role/status
INSERT INTO p_member (id, role, email, password, nickname, auction_cancel_count, status, created_at, updated_at, deleted_at, deleted_by)
VALUES
    (1, 'ADMIN', 'admin@example.com', '$2a$10$sFHAFHXkl0ohQkv3uCuOAOmsfZgI/Aca4FH6hnkKP6jj1Fqw1Prg2', 'admin', 0, 'ACTIVE', NOW(), NOW(), NULL, NULL),
    (2, 'SELLER', 'seller@seller.com', '$2b$12$Hs3mLffihQf6SQi40osFGetwp1j6o.dIApD5T4IQOB2m3Ezj8Cmf.', 'seller', 0, 'ACTIVE', NOW(), NOW(), NULL, NULL),
    (4, 'CONSUMER', 'consumer_changed_from_seller4@example.com', 'hashed-consumer-password', 'consumer_from4', 0, 'ACTIVE', NOW(), NOW(), NULL, NULL)
ON CONFLICT DO NOTHING;

-- Seed sellers by status (linked to seller members above)
INSERT INTO p_seller (id, member_id, account_num, status, amount, created_at, updated_at, deleted_at, deleted_by)
VALUES
    ('00000000-0000-0000-0000-000000000001', 4, '111-11-111111', 'PENDING', 0, NOW(), NOW(), NULL, NULL),
    ('00000000-0000-0000-0000-000000000002', 2, '222-22-222222', 'ACTIVE', 3000.00, NOW(), NOW(), NULL, NULL)
ON CONFLICT DO NOTHING;

-- Seed: seller outbox pending 230 rows
-- INSERT INTO p_seller_outbox (
--     created_at,
--     error_message,
--     event_type,
--     member_id,
--     payload,
--     processed_at,
--     retry_count,
--     status
-- )
-- SELECT
--     NOW(),
--     NULL,
--     'seller.register.v1',
--     200000 + g,                             -- dummy member id
--     json_build_object('seed', g)::json,     -- dummy payload
--     NULL,
--     0,
--     'PENDING'
-- FROM generate_series(1, 230) g;

-- Event seed: member id 4 changed to consumer
INSERT INTO p_seller_outbox (
    created_at,
    error_message,
    event_type,
    member_id,
    payload,
    processed_at,
    retry_count,
    status
)
VALUES (
    NOW(),
    NULL,
    'seller.register.v1',
    4,
    json_build_object(
        'memberId', 4,
        'idempotencyKey', '00000000-0000-0000-0000-000000000004',
        -- LocalDateTime shape (no offset) for dummy event
        'createdAt', NOW()::timestamp
    )::json,
    NULL,
    0,
    'PENDING'
);

-- Reset sequence after manual inserts to avoid PK collisions
SELECT setval(
    pg_get_serial_sequence('p_member', 'id'),
    (SELECT COALESCE(MAX(id), 0) + 1 FROM p_member),
    false
);
