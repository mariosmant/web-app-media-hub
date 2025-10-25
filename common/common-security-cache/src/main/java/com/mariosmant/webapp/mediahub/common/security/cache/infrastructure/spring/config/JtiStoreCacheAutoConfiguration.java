package com.mariosmant.webapp.mediahub.common.security.cache.infrastructure.spring.config;

import com.mariosmant.webapp.mediahub.common.security.context.domain.service.JtiStore;
import com.mariosmant.webapp.mediahub.common.security.cache.CacheJtiStore;
import org.springframework.cache.CacheManager;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@AutoConfiguration
@ConditionalOnProperty(prefix = "security.validator-policy", name = "jti", havingValue = "true", matchIfMissing = true)
@ConditionalOnProperty(prefix = "security.validator-policy", name = "jti-store-method", havingValue = "cache", matchIfMissing = true)
public class JtiStoreCacheAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public JtiStore jtiStore(CacheManager cacheManager) {
        return new CacheJtiStore(cacheManager);
    }
}