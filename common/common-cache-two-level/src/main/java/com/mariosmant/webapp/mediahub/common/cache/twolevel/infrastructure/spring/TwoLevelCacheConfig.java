package com.mariosmant.webapp.mediahub.common.cache.twolevel.infrastructure.spring;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConditionalOnProperty(prefix = "app.cache.two-level", name = "enabled", havingValue = "true")
@EnableConfigurationProperties({AppCacheTwoLevelProperties.class})
public class TwoLevelCacheConfig {
    @Bean(name = {"twoLevelCacheManager"})
    @ConditionalOnProperty(prefix = "app.cache.compose", name = "enabled", havingValue = "true")
    public CacheManager twoLevelCacheManager(
            @Qualifier("l1CacheManager") CacheManager l1CacheManager,
            @Qualifier("l2CacheManager") CacheManager l2CacheManager,
            AppCacheTwoLevelProperties appCacheTwoLevelProperties) {
        return commonTwoLevelCacheManager(l1CacheManager, l2CacheManager, appCacheTwoLevelProperties);
    }

    @Bean(name = {"twoLevelCacheManager", "cacheManager"})
    @ConditionalOnProperty(prefix = "app.cache.compose", name = "enabled", havingValue = "false", matchIfMissing = true)
    public CacheManager twoLevelCacheManagerDefault(
            @Qualifier("l1CacheManager") CacheManager l1CacheManager,
            @Qualifier("l2CacheManager") CacheManager l2CacheManager,
            AppCacheTwoLevelProperties appCacheTwoLevelProperties) {
        return commonTwoLevelCacheManager(l1CacheManager, l2CacheManager, appCacheTwoLevelProperties);
    }

    private CacheManager commonTwoLevelCacheManager(CacheManager l1CacheManager,
                                                    CacheManager l2CacheManager,
                                                    AppCacheTwoLevelProperties appCacheTwoLevelProperties) {
        List<TwoLevelCache> caches = appCacheTwoLevelProperties.getCaches().stream()
                .map(name -> new TwoLevelCache(name, l1CacheManager.getCache(name), l2CacheManager.getCache(name)))
                .toList();

        SimpleCacheManager mgr = new SimpleCacheManager();
        mgr.setCaches(caches);
        return mgr;
    }

}
