-- KEYS[1] = winner:{bidId}:{allocationKey}
-- KEYS[2] = idem:{bidId}:{idempotencyKey}
-- return 1 always (idempotent)

redis.call('DEL', KEYS[1])
redis.call('DEL', KEYS[2])
return 1