-- KEYS[1] = bucket key
-- ARGV[1] = capacity
-- ARGV[2] = refill_per_sec
-- ARGV[3] = now_ms
-- ARGV[4] = cost
-- Returns: {allowed, remaining, retry_after_ms}

local capacity = tonumber(ARGV[1])
local refill_per_sec = tonumber(ARGV[2])
local now_ms = tonumber(ARGV[3])
local cost = tonumber(ARGV[4])

local data = redis.call('HMGET', KEYS[1], 'tokens', 'ts')
local tokens = tonumber(data[1])
local ts = tonumber(data[2])

if tokens == nil then
  tokens = capacity
  ts = now_ms
end

local elapsed = (now_ms - ts) / 1000.0
tokens = math.min(capacity, tokens + elapsed * refill_per_sec)
ts = now_ms

if tokens >= cost then
  tokens = tokens - cost
  redis.call('HMSET', KEYS[1], 'tokens', tokens, 'ts', ts)
  return {1, math.floor(tokens), 0}
else
  local retry = math.ceil(((cost - tokens) / refill_per_sec) * 1000)
  redis.call('HMSET', KEYS[1], 'tokens', tokens, 'ts', ts)
  return {0, math.floor(tokens), retry}
end
