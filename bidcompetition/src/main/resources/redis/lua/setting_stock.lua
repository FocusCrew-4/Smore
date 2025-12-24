-- KEYS[1] = stock:{bidId}
-- ARGV[1] = stockQuantity

local stock = tonumber(ARGV[1])
if not stock or stock < 0 then
  return -1
end

if redis.call('EXISTS', KEYS[1]) == 1 then
  return 0
end

redis.call('SET', KEYS[1], stock)
return stock
