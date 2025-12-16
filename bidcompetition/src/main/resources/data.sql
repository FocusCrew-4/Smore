INSERT INTO p_bid_competition (
    id,
    product_id,
    category_id,
    seller_id,
    product_price,
    total_quantity,
    stock,
    bid_status,
    idempotency_key,
    start_at,
    end_at,
    created_at,
    updated_at
)
VALUES
    (
        '11111111-aaaa-1111-aaaa-111111111111',
        'aaaabbbb-aaaa-bbbb-aaaa-aaaaaaaaaaaa',
        'ccccdddd-cccc-dddd-cccc-dddddddddddd',
        2001,
        15000,
     50,
        50,
        'SCHEDULED',  -- CREATED → SCHEDULED
        'aaaa1111-aaaa-2222-aaaa-222222222222',
        NOW(),
        NOW() + INTERVAL '1 day',
        NOW(),
        NOW()
    ),
    (
        '22222222-bbbb-2222-bbbb-222222222222',
        'bbbbcccc-bbbb-cccc-bbbb-cccccccccccc',
        'ddddaaaa-dddd-aaaa-dddd-aaaaaaaaaaaa',
        2002,
        32000,
        120,
     120,
        'ACTIVE',    -- OPEN → ACTIVE
        'bbbb2222-bbbb-3333-bbbb-333333333333',
        NOW() - INTERVAL '1 hour',
        NOW() + INTERVAL '6 hours',
        NOW(),
        NOW()
    ),
    (
        '33333333-cccc-3333-cccc-333333333333',
        'ccccdddd-cccc-dddd-cccc-dddddddddddd',
        'eeeeffff-eeee-ffff-eeee-ffffffffffff',
        2003,
        8000,
        20,
     1,
        'ACTIVE',    -- enum에 있음, 그대로 사용
        'cccc3333-cccc-4444-cccc-444444444444',
        NOW() - INTERVAL '2 days',
        NOW() - INTERVAL '1 day',
        NOW(),
        NOW()
    ),
    (
        '44444444-cccc-4444-cccc-444444444444',
        'ccccdddd-aaaa-dddd-aaaa-dddddddddddd',
        'eeeeffff-eeee-ffff-eeee-ffffffffffff',
        2003,
        15000,
        120,
        50,
        'CLOSED',    -- enum에 있음, 그대로 사용
        'aaaa3333-aaaa-4444-dddd-444444444445',
        NOW() - INTERVAL '2 days',
        NOW() + INTERVAL '1 day',
        NOW(),
        NOW()
    ),
    (
        '55555555-cccc-5555-cccc-555555555555',
        'ccccdddd-aaaa-dddd-aaaa-ddddddddddaa',
        'eeeeffff-eeee-ffff-eeee-ffffffffffff',
        2004,
        500000,
        300,
     0,
        'ACTIVE',    -- enum에 있음, 그대로 사용
        'aaaa3333-aaaa-4444-dddd-444444444446',
        NOW() - INTERVAL '2 days',
        NOW() + INTERVAL '1 day',
        NOW(),
        NOW()
    );
