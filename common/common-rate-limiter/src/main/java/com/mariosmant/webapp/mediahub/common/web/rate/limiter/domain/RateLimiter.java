package com.mariosmant.webapp.mediahub.common.web.rate.limiter.domain;

public interface RateLimiter {
    RateLimitDecision check(String key, long cost);
}
