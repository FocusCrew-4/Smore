-- Dummy data for development fee, cancel, and refund policies

-- Fee policies
INSERT INTO fee_policies (
    id,
    target_type,
    target_key,
    fee_type,
    rate,
    fixed_amount,
    active,
    created_at,
    updated_at
) VALUES
    (
        gen_random_uuid(),
        'MERCHANT',
        '2002',
        'RATE',
        0.02,
        500.0,
        true,
        now(),
        now()
    );

-- Cancel policies
INSERT INTO cancel_policies (
    id,
    cancel_target_type,
    target_key,
    cancel_limit_minutes,
    cancel_fee_type,
    rate,
    fixed_amount,
    cancellable,
    active,
    created_at,
    updated_at
) VALUES
      (
          gen_random_uuid(),
          'MERCHANT',
          '2002',
          1800000000000,   -- 3 days = 259,200 seconds = 259,200,000,000,000 ns
          'MIXED',
          0.04,
          300,
          false,
          true,
          now(),
          now()
      ),
      (
          gen_random_uuid(),
          'AUCTION_TYPE',
          'BID',
          1800000000000,   -- 30 minutes = 1800초 = 1,800,000,000,000 ns
          'MIXED',
          0.03,
          250,
          true,
          true,
          now(),
          now()
      ),
      (
          gen_random_uuid(),
          'AUCTION_TYPE',
          'AUCTION',
          10000000000,   -- 1 minutes = 1800초 = 1,800,000,000,000 ns
          'RATE',
          100.0,
          0.0,
          false,
          true,
          now(),
          now()
      );

-- Refund policies
INSERT INTO refund_policies (
    id,
    refund_target_type,
    target_key,
    refund_period_days,
    refund_fee_type,
    rate,
    fixed_amount,
    refundable,
    active,
    created_at,
    updated_at
) VALUES
      (
          gen_random_uuid(),
          'MERCHANT',
          '2002',
          259200000000000,   -- 3 days = 259,200 seconds = 259,200,000,000,000 ns
          'MIXED',
          0.04,
          300,
          false,
          true,
          now(),
          now()
      ),
      (
          gen_random_uuid(),
          'AUCTION_TYPE',
          'BID',
          259200000000000,   -- 3 days = 259,200 seconds = 259,200,000,000,000 ns
          'MIXED',
          0.04,
          300,
          true,
          true,
          now(),
          now()
      ),
      (
          gen_random_uuid(),
          'AUCTION_TYPE',
          'AUCTION',
          259200000000000,   -- 3 days = 259,200 seconds = 259,200,000,000,000 ns
          'MIXED',
          0.04,
          300,
          false,
          true,
          now(),
          now()
      );
