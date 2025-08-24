package com.mariosmant.webapp.mediahub.common.rate.limiter.token.bucket.infrastructure.spring;

import com.mariosmant.webapp.mediahub.common.rate.limiter.domain.RateLimiter;
import com.mariosmant.webapp.mediahub.common.rate.limiter.token.bucket.domain.TokenBucketRateLimiter;
import com.mariosmant.webapp.mediahub.common.rate.limiter.infrastructure.spring.RateLimiterProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
@ConditionalOnProperty(name = "rate-limiter.algorithm", havingValue = "token-bucket")
public class TokenBucketAutoConfiguration {

    @Bean
    public RateLimiter tokenBucketRateLimiter(StringRedisTemplate redis, RateLimiterProperties props) {
        return new TokenBucketRateLimiter(redis, props);
    }
}
