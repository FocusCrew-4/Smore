INSERT INTO p_product (
    id,
    seller_id,
    category_id,
    name,
    description,
    price,
    stock,
    sale_type,
    threshold_for_auction,
    status,
    created_at,
    updated_at,
    deleted_at,
    deleted_by,
    start_at,
    end_at,
    bidding_duration
)
VALUES (
           'f4b53413-a635-4d15-b31f-d678ac533e12',
           1,
           '11111111-1111-1111-1111-111111111111',
           'Sample Product',
           'This is a sample description.',
           10000.00,
           10,
           'NORMAL',
           5,
           'ON_SALE',
           NOW(),
           NOW(),
           NULL,
           NULL,
           NOW(),
           NOW() + INTERVAL '1 day',
           NULL
       );

INSERT INTO p_product_stock_log (
    id,
    product_id,
    before_stock,
    after_stock,
    created_at
)
VALUES (
           'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
           'f4b53413-a635-4d15-b31f-d678ac533e12',
           100,
           90,
           NOW()
       );

INSERT INTO p_product_sale (
    id,
    product_id,
    quantity,
    price_at_purchase,
    created_at
)
VALUES (
           'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
           'f4b53413-a635-4d15-b31f-d678ac533e12',
           1,
           29000,
           NOW()
       );
