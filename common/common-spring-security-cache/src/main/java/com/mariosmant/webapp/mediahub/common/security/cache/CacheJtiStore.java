package com.mariosmant.webapp.mediahub.common.security.cache;

import com.mariosmant.webapp.mediahub.common.security.context.domain.service.JtiStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

public class CacheJtiStore implements JtiStore {
    private final Cache jtiCache;

    public CacheJtiStore(CacheManager cacheManager) {
        this.jtiCache = cacheManager.getCache("jtiCache");
    }

    @Override
    public boolean seen(String jti) {
        return jtiCache.get(jti) != null;
    }

    @Override
    public void remember(String jti) {
        jtiCache.put(jti, Boolean.TRUE);
    }
}
