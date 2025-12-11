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
          '1b5e6f7a-8b9c-4dad-9e0f-1a2b3c4d5e6f',
          'CATEGORY',
          '11111111-1111-1111-1111-111111111111',
          'RATE',
          0.02,
          NULL,
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
          '3d8e9f0a-1b2c-4d3e-9f4a-5b6c7d8e9f0a',
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
          '3d8e9f0a-1b2c-4d3e-9f4a-5b6c7d8e9f0b',
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
          '5f0a1b2c-3d4e-5f6a-9b7c-8d9e0f1a2b3c',
          'AUCTION_TYPE',
          'BID',
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
          '5f0a1b2c-3d4e-5f6a-9b7c-8d9e0f1a2b3d',
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
