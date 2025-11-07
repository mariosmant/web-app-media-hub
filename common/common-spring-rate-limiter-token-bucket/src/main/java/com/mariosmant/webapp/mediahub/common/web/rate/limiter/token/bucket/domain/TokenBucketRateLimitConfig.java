package com.mariosmant.webapp.mediahub.common.web.rate.limiter.token.bucket.domain;

import com.mariosmant.webapp.mediahub.common.web.rate.limiter.domain.BaseRateLimitConfig;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Token Bucket–specific configuration.
 */
@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class TokenBucketRateLimitConfig extends BaseRateLimitConfig {
    private Long capacity;
    private Long refillPerSec;
}
