-- 환불 시 재고 복구 (winner/idempotency 키가 이미 정리된 경우용)
-- KEYS[1] = stock:{bidId}
-- KEYS[2] = refund:{bidId}:{refundId} (idempotency)
-- ARGV[1] = quantity
--
-- return:
--  1 restored
--  0 already restored (idempotent)
-- -3 invalid args

local quantity = tonumber(ARGV[1])
if (not quantity) or quantity <= 0 then
  return -3
end

-- 이미 복구된 환불이면 재실행 방지
if redis.call('EXISTS', KEYS[2]) == 1 then
  return 0
end

redis.call('INCRBY', KEYS[1], quantity)
redis.call('SET', KEYS[2], '1', 'EX', 60 * 60 * 24) -- 1 day TTL for traceability

return 1
