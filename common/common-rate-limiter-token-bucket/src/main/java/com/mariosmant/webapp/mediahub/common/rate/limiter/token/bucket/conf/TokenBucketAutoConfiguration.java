package com.mariosmant.webapp.mediahub.common.rate.limiter.token.bucket.conf;

import com.mariosmant.webapp.mediahub.common.rate.limiter.RateLimiter;
import com.mariosmant.webapp.mediahub.common.rate.limiter.token.bucket.TokenBucketRateLimiter;
import com.mariosmant.webapp.mediahub.common.rate.limiter.conf.RateLimiterProperties;
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
