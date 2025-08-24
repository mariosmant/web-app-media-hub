package com.mariosmant.webapp.mediahub.common.rate.limiter.domain;

public interface RateLimiter {
    RateLimitDecision check(String key, long cost);
}
