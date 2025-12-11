-- Dummy seed data for local/dev profiles
-- Keep UUIDs stable so related rows stay consistent across tables.

-- Auctions
INSERT INTO p_auction (id, product_id, product_price, stock, seller_id, status, created_at, updated_at, deleted_at, deleted_by) VALUES
('11111111-1111-1111-1111-111111111111', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 1000.00, 10, 1001, 'READY',  '2024-11-01 10:00:00', '2024-11-01 10:00:00', NULL, NULL),
('22222222-2222-2222-2222-222222222222', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 2500.50, 5,  1002, 'OPEN',   '2024-11-02 12:00:00', '2024-11-03 09:15:00', NULL, NULL),
('33333333-3333-3333-3333-333333333333', 'cccccccc-cccc-cccc-cccc-cccccccccccc',  500.00, 3,  1003, 'CLOSED', '2024-11-03 08:30:00', '2024-11-05 18:45:00', NULL, NULL);

-- Bidder ranks for the closed auction (stock = 3 -> ranks 1~3 are WINNER, others STANDBY)
INSERT INTO p_auction_bidder_rank (id, auction_id, bidder_id, bidder_price, bidder_quantity, bidder_status, rank, created_at, updated_at, deleted_at, deleted_by) VALUES
('44444444-4444-4444-4444-444444444441', '33333333-3333-3333-3333-333333333333', 7001, 650.00, 1, 'WINNER',  1, '2024-11-05 11:05:00', '2024-11-05 11:05:00', NULL, NULL),
('44444444-4444-4444-4444-444444444442', '33333333-3333-3333-3333-333333333333', 7002, 620.00, 1, 'WINNER',  2, '2024-11-05 11:06:00', '2024-11-05 11:06:00', NULL, NULL),
('44444444-4444-4444-4444-444444444443', '33333333-3333-3333-3333-333333333333', 7003, 610.00, 1, 'WINNER',  3, '2024-11-05 11:07:00', '2024-11-05 11:07:00', NULL, NULL),
('44444444-4444-4444-4444-444444444444', '33333333-3333-3333-3333-333333333333', 7004, 580.00, 1, 'STANDBY', 4, '2024-11-05 11:08:00', '2024-11-05 11:08:00', NULL, NULL);
