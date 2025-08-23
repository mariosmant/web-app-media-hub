package com.mariosmant.webapp.mediahub.common.rate.limiter.conf;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@NoArgsConstructor
@ConfigurationProperties("rate-limiter")
public class RateLimiterProperties {
    private String prefix = "rl";
    private Long capacity = 100L;
    private Double refillPerSec = 2.0;
    private Long windowSeconds = 60L;
}
