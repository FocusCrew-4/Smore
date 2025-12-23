-- KEYS[1] = stock:{bidId}
-- KEYS[2] = winner:{bidId}:{allocationKey}
-- KEYS[3] = idem:{bidId}:{idempotencyKey}
-- ARGV[1] = quantity
--
-- return:
--  1 rolled back
--  0 nothing to rollback (already done / no hold)
-- -3 invalid args

local quantity = tonumber(ARGV[1])
if (not quantity) or quantity <= 0 then
  return -3
end

-- winner가 있을 때만 재고 복구 (중복복구 방지)
if redis.call('DEL', KEYS[2]) == 1 then
  redis.call('INCRBY', KEYS[1], quantity)
  redis.call('DEL', KEYS[3]) -- 실패면 재시도 허용(권장)
  return 1
end

return 0
