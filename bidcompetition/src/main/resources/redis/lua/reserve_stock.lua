-- KEYS[1] = stock:{bidId}
-- KEYS[2] = winner:{bidId}:{allocationKey}
-- KEYS[3] = idem:{bidId}:{idempotencyKey}
--
-- ARGV[1] = userId
-- ARGV[2] = quantity
-- ARGV[3] = winnerTtlSeconds
-- ARGV[4] = idemTtlSeconds

--
-- return:
--  1  success
--  0  insufficient / invalid
-- -2 duplicate (idem)

if redis.call('EXISTS', KEYS[3]) == 1 then
  return -2
end

local stockStr = redis.call('GET', KEYS[1])
if not stockStr then
  return 0
end

local stock = tonumber(stockStr)
local quantity = tonumber(ARGV[2])
local winnerTtlSeconds = tonumber(ARGV[3])
local idemTtlSeconds = tonumber(ARGV[4])

if (not stock) or (not quantity) or quantity <= 0 or (not winnerTtlSeconds) or winnerTtlSeconds <= 0 or (not idemTtlSeconds) or idemTtlSeconds <= 0 then
  return 0
end

if stock < quantity then
  return 0
end

redis.call('DECRBY', KEYS[1], quantity)
redis.call('SET', KEYS[2], ARGV[1], 'EX', winnerTtlSeconds)
redis.call('SET', KEYS[3], '1', 'EX', idemTtlSeconds)

return 1