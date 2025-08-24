package com.mariosmant.webapp.mediahub.common.rate.limiter.domain;

public record RateLimitDecision(boolean allowed, long remaining, long retryAfterMs) {}
