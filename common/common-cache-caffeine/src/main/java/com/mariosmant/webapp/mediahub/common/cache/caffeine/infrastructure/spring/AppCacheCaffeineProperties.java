package com.mariosmant.webapp.mediahub.common.cache.caffeine.infrastructure.spring;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ConfigurationProperties(prefix = "app.cache.caffeine")
public class AppCacheCaffeineProperties {
    private Boolean transactionAware;
    private List<CaffeineSpecProperties> caches = new ArrayList<>();

    @Setter
    @Getter
    public static class CaffeineSpecProperties {
        private String name;
        private long maximumSize;
        private Duration expireAfterWrite;
    }
}
