package com.mariosmant.webapp.mediahub.common.cache.caffeine.infrastructure.spring;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.cache.transaction.TransactionAwareCacheManagerProxy;
import org.springframework.context.annotation.Bean;

import java.util.List;

@AutoConfiguration
@ConditionalOnClass(CaffeineCacheManager.class)
@ConditionalOnProperty(prefix = "app.cache.caffeine", name = "enabled", havingValue = "true")
@EnableConfigurationProperties({AppCacheCaffeineProperties.class})
public class CaffeineCacheConfig {

    @Bean(name = {"caffeineCacheManager", "l1CacheManager", "primaryCacheManager"})
    @ConditionalOnProperty(prefix = "app.cache.caffeine", name = "isPrimary", havingValue = "true")
    public CacheManager caffeineCacheManagerPrimary(AppCacheCaffeineProperties appCacheCaffeineProperties) {
        return commonCaffeineCacheManager(appCacheCaffeineProperties);
    }

    @Bean(name = {"caffeineCacheManager", "l2CacheManager", "secondaryCacheManager"})
    @ConditionalOnProperty(prefix = "app.cache.caffeine", name = "isSecondary", havingValue = "true")
    public CacheManager caffeineCacheManagerSecondary(AppCacheCaffeineProperties appCacheCaffeineProperties) {
        return commonCaffeineCacheManager(appCacheCaffeineProperties);
    }

    @Bean(name = {"caffeineCacheManager", "cacheManager"})
    @ConditionalOnProperty(prefix = "app.cache.caffeine", name = "isPrimary", havingValue = "false", matchIfMissing = true)
    @ConditionalOnProperty(prefix = "app.cache.caffeine", name = "isSecondary", havingValue = "false", matchIfMissing = true)
    @ConditionalOnProperty(prefix = "app.cache.two-level", name = "enabled", havingValue = "false", matchIfMissing = true)
    @ConditionalOnProperty(prefix = "app.cache.composite", name = "enabled", havingValue = "false", matchIfMissing = true)
    public CacheManager caffeineCacheManager(AppCacheCaffeineProperties appCacheCaffeineProperties) {
        return commonCaffeineCacheManager(appCacheCaffeineProperties);
    }

    private CacheManager commonCaffeineCacheManager(AppCacheCaffeineProperties appCacheCaffeineProperties) {
        List<CaffeineCache> caches = appCacheCaffeineProperties.getCaches().stream()
                .map(spec -> new CaffeineCache(
                        spec.getName(),
                        Caffeine.newBuilder()
                                .maximumSize(spec.getMaximumSize())
                                .expireAfterWrite(spec.getExpireAfterWrite())
                                .build()
                ))
                .toList();

        SimpleCacheManager manager = new SimpleCacheManager();
        manager.setCaches(caches);
        return Boolean.TRUE.equals(appCacheCaffeineProperties.getTransactionAware()) ? new TransactionAwareCacheManagerProxy(manager) : manager;
    }
}
