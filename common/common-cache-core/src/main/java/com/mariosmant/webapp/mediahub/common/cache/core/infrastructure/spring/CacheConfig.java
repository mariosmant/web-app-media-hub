package com.mariosmant.webapp.mediahub.common.cache.core.infrastructure.spring;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.support.CompositeCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.util.ArrayList;
import java.util.List;

@AutoConfiguration
@EnableCaching
public class CacheConfig {

    @Bean
    @Primary
    @ConditionalOnProperty(prefix = "app.cache.compose", name = "enabled", havingValue = "true")
    public CacheManager cacheManager(
            @Qualifier("twoCacheConfigCacheManager") ObjectProvider<CacheManager> twoCacheConfigCacheManager,
            @Qualifier("primaryCacheManager") ObjectProvider<CacheManager> primaryCacheManager,
            @Qualifier("secondaryCacheManager") ObjectProvider<CacheManager> secondaryCacheManager) {
        List<CacheManager> managers = new ArrayList<>();
        twoCacheConfigCacheManager.ifAvailable(managers::add);
        primaryCacheManager.ifAvailable(managers::add);
        secondaryCacheManager.ifAvailable(managers::add);

        CompositeCacheManager composite = new CompositeCacheManager(managers.toArray(new CacheManager[0]));
        composite.setFallbackToNoOpCache(false); // Fail if a cache is not found.
        return composite;
    }

}
