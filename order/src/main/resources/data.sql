INSERT INTO p_order (
    id, user_id,
    product_id, product_price,
    quantity, total_amount,

    payment_id, category_id, sale_type,

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

    NULL, '9f3c3a4c-8b6c-4f9a-9f4f-0e4c62e2b3c7', 'BID',

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

 'pay_3f92a7b8-12cd-4c7f-9e10-83ac912f4bb1', 'c1c9d8d2-7e54-4c1e-a7e8-61c5ff0bb2fa', 'BID',

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

    'pay_9bd1c6ef-45aa-4f1e-88ce-2a7df301cb84', '4c0c2c0f-3f7a-4db3-92c2-6bd7d8e4fb9c', 'AUCTION',

    0, 0, 0, 0,

    'cccc3333-cccc-3333-cccc-333333333333',
    'COMPLETED', 'NONE',

    NOW(), NULL, NULL,

    'Mapo-daero 44', 'Seoul', '04100',

    NOW(), NOW()
);
