package com.mariosmant.webapp.mediahub.common.rate.limiter;

public interface RateLimiter {
    RateLimitDecision check(String key, long cost);
}
