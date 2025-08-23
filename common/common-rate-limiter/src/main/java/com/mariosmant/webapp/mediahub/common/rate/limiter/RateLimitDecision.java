package com.mariosmant.webapp.mediahub.common.rate.limiter;

public record RateLimitDecision(boolean allowed, long remaining, long retryAfterMs) {}
