package com.mariosmant.webapp.mediahub.common.web.rate.limiter.token.bucket.domain;

import com.mariosmant.webapp.mediahub.common.web.rate.limiter.domain.RateLimitDecision;
import com.mariosmant.webapp.mediahub.common.web.rate.limiter.domain.RateLimiter;
import com.mariosmant.webapp.mediahub.common.web.rate.limiter.infrastructure.spring.redis.RedisScriptLoader;
import com.mariosmant.webapp.mediahub.common.web.rate.limiter.infrastructure.spring.RateLimiterProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.List;

public class TokenBucketRateLimiter implements RateLimiter {

    private final StringRedisTemplate redis;
    private final RateLimiterProperties props;
    private final DefaultRedisScript<List> script;

    public TokenBucketRateLimiter(StringRedisTemplate redis, RateLimiterProperties props) {
        this.redis = redis;
        this.props = props;
        this.script = RedisScriptLoader.load("redis-scripts/token_bucket.lua", List.class);
    }

    @Override
    public RateLimitDecision check(String key, long cost) {
        List<?> res = redis.execute(script, List.of(props.getPrefix() + ":tb:" + key),
                String.valueOf(props.getCapacity()),
                String.valueOf(props.getRefillPerSec()),
                String.valueOf(System.currentTimeMillis()),
                String.valueOf(cost));
        long allowed = Long.parseLong(res.get(0).toString());
        long remaining = Long.parseLong(res.get(1).toString());
        long retryMs = Long.parseLong(res.get(2).toString());
        return new RateLimitDecision(allowed == 1, remaining, retryMs);
    }
}
