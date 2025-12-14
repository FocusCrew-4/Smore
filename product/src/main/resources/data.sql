CREATE TABLE IF NOT EXISTS p_product_stock_log
(
    id           UUID PRIMARY KEY,
    product_id   UUID      NOT NULL,
    before_stock INT       NOT NULL,
    after_stock  INT       NOT NULL,
    created_at   TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS p_product_sale
(
    id                UUID PRIMARY KEY,
    product_id        UUID           NOT NULL,
    quantity          INT            NOT NULL,
    price_at_purchase NUMERIC(10, 2) NOT NULL,
    created_at        TIMESTAMP      NOT NULL
);


INSERT INTO p_product_stock_log (id, product_id, before_stock, after_stock, created_at)
VALUES (gen_random_uuid(),
        'f4b53413-a635-4d15-b31f-d678ac533e38',
        100,
        90,
        NOW());



INSERT INTO p_product_sale (id, product_id, quantity, price_at_purchase, created_at)
VALUES (gen_random_uuid(),
        'f4b53413-a635-4d15-b31f-d678ac533e38',
        1,
        29000,
        NOW());

INSERT INTO p_product (id,
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
                       bidding_duration)
VALUES ('f4b53413-a635-4d15-b31f-d678ac533e38',
        1, -- sellerId
        '11111111-1111-1111-1111-111111111111', -- categoryId (원하면 실제 값 넣어줘)
        'Sample Product',
        'This is a sample description.',
        10000.00,
        10,
        'NORMAL', -- SaleType enum: FIXED / BID 등
        5, -- thresholdForAuction
        'ON_SALE', -- ProductStatus enum
        NOW(), -- createdAt
        NOW(), -- updatedAt
        NULL, -- deletedAt
        NULL, -- deletedBy
        NOW(), -- startAt
        NOW() + INTERVAL '1 day', -- endAt
        NULL -- biddingDuration
       );
