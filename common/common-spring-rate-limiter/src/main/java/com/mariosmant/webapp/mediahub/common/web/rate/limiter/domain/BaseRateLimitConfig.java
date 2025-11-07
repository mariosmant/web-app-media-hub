package com.mariosmant.webapp.mediahub.common.web.rate.limiter.domain;

import lombok.Data;

/**
 * Common base configuration for all rate limiter configs.
 */
@Data
public abstract class BaseRateLimitConfig {
    private String prefix;
    private String fullKey;
}
