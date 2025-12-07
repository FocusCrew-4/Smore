INSERT INTO p_order (
    id, user_id,
    product_id, product_price,
    quantity, total_amount,

    refund_reserved_quantity, refunded_quantity, refunded_amount, fee_amount,

    idempotency_key,
    order_status, cancel_status,

    ordered_at, confirmed_at, cancelled_at,

    street, city, zipcode,

    created_at, updated_at
)
VALUES
-- 1번 주문 (CREATED)
(
    '11111111-1111-1111-1111-111111111111', 1001,
    'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 12000,
    1, 12000,

    0, 0, 0, 0,

    'aaaa1111-aaaa-1111-aaaa-111111111111',
    'CREATED', 'NONE',

    NOW(), NULL, NULL,

    'Gangnam-daero 1', 'Seoul', '06000',

    NOW(), NOW()
),

-- 2번 주문 (CONFIRMED)
(
    '22222222-2222-2222-2222-222222222222', 1002,
    'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 32000,
    3, 96000,

    0, 0, 0, 0,

    'bbbb2222-bbbb-2222-bbbb-222222222222',
    'CONFIRMED', 'NONE',

    NOW(), NULL, NULL,

    'Teheran-ro 12', 'Seoul', '06100',

    NOW(), NOW()
),

-- 3번 주문 (COMPLETED)
(
    '33333333-3333-3333-3333-333333333333', 1003,
    'cccccccc-cccc-cccc-cccc-cccccccccccc', 5000,
    5, 25000,

    0, 0, 0, 0,

    'cccc3333-cccc-3333-cccc-333333333333',
    'COMPLETED', 'NONE',

    NOW(), NULL, NULL,

    'Mapo-daero 44', 'Seoul', '04100',

    NOW(), NOW()
);
