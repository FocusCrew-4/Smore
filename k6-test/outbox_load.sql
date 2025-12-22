-- Outbox load test markers + dummy load for auction.
-- Run one block per test size (10k/100k/1m).
-- Note: payload column is json, so markers are JSON strings.

-- 10k
INSERT INTO p_auction_outbox (
    event_type,
    member_id,
    payload,
    status,
    retry_count,
    created_at
)
VALUES (
    'TEST_MARK',
    1001,
    '"TEST_START"',
    'PENDING',
    0,
    now()
);

INSERT INTO p_auction_outbox (
    event_type,
    member_id,
    payload,
    status,
    retry_count,
    created_at
)
SELECT
    'AUCTION_CONFIRMED',
    1001,
    '{"dummy": true}',
    'PENDING',
    0,
    now()
FROM generate_series(1, 10000);

INSERT INTO p_auction_outbox (
    event_type,
    member_id,
    payload,
    status,
    retry_count,
    created_at
)
VALUES (
    'TEST_MARK',
    1001,
    '"TEST_END"',
    'PENDING',
    0,
    now()
);

-- 100k
INSERT INTO p_auction_outbox (
    event_type,
    member_id,
    payload,
    status,
    retry_count,
    created_at
)
VALUES (
    'TEST_MARK',
    1001,
    '"TEST_START"',
    'PENDING',
    0,
    now()
);

INSERT INTO p_auction_outbox (
    event_type,
    member_id,
    payload,
    status,
    retry_count,
    created_at
)
SELECT
    'AUCTION_CONFIRMED',
    1001,
    '{"dummy": true}',
    'PENDING',
    0,
    now()
FROM generate_series(1, 100000);

INSERT INTO p_auction_outbox (
    event_type,
    member_id,
    payload,
    status,
    retry_count,
    created_at
)
VALUES (
    'TEST_MARK',
    1001,
    '"TEST_END"',
    'PENDING',
    0,
    now()
);

-- 1m
INSERT INTO p_auction_outbox (
    event_type,
    member_id,
    payload,
    status,
    retry_count,
    created_at
)
VALUES (
    'TEST_MARK',
    1001,
    '"TEST_START"',
    'PENDING',
    0,
    now()
);

INSERT INTO p_auction_outbox (
    event_type,
    member_id,
    payload,
    status,
    retry_count,
    created_at
)
SELECT
    'AUCTION_CONFIRMED',
    1001,
    '{"dummy": true}',
    'PENDING',
    0,
    now()
FROM generate_series(1, 1000000);

INSERT INTO p_auction_outbox (
    event_type,
    member_id,
    payload,
    status,
    retry_count,
    created_at
)
VALUES (
    'TEST_MARK',
    1001,
    '"TEST_END"',
    'PENDING',
    0,
    now()
);
