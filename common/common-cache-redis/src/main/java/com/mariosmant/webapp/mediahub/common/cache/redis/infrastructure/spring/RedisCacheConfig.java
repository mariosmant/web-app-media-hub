package com.mariosmant.webapp.mediahub.common.cache.redis.infrastructure.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.*;

import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@ConditionalOnClass(RedisCacheManager.class)
@ConditionalOnProperty(prefix = "app.cache.redis", name = "enabled", havingValue = "true")
@EnableConfigurationProperties({AppCacheRedisProperties.class})
public class RedisCacheConfig {

    @Bean(name = {"redisCacheManager", "l1CacheManager", "primaryCacheManager"})
    @ConditionalOnProperty(prefix = "app.cache.redis", name = "isPrimary", havingValue = "true")
    public RedisCacheManager redisCacheManagerPrimary(RedisConnectionFactory connectionFactory, AppCacheRedisProperties appCacheRedisProperties, ObjectMapper objectMapper) {
        return commonRedisCacheManager(connectionFactory, appCacheRedisProperties, objectMapper);
    }

    @Bean(name = {"redisCacheManager", "l2CacheManager", "secondaryCacheManager"})
    @ConditionalOnProperty(prefix = "app.cache.redis", name = "isSecondary", havingValue = "true")
    public RedisCacheManager redisCacheManagerSecondary(RedisConnectionFactory connectionFactory, AppCacheRedisProperties appCacheRedisProperties, ObjectMapper objectMapper) {
        return commonRedisCacheManager(connectionFactory, appCacheRedisProperties, objectMapper);
    }

    @Bean(name = {"redisCacheManager", "cacheManager"})
    @ConditionalOnProperty(prefix = "app.cache.redis", name = "isPrimary", havingValue = "false", matchIfMissing = true)
    @ConditionalOnProperty(prefix = "app.cache.redis", name = "isSecondary", havingValue = "false", matchIfMissing = true)
    @ConditionalOnProperty(prefix = "app.cache.two-level", name = "enabled", havingValue = "false", matchIfMissing = true)
    @ConditionalOnProperty(prefix = "app.cache.composite", name = "enabled", havingValue = "false", matchIfMissing = true)
    public RedisCacheManager redisCacheManager(RedisConnectionFactory connectionFactory, AppCacheRedisProperties appCacheRedisProperties, ObjectMapper objectMapper) {
        return commonRedisCacheManager(connectionFactory, appCacheRedisProperties, objectMapper);
    }

    private RedisCacheManager commonRedisCacheManager(@Qualifier("redisConnectionFactory") RedisConnectionFactory connectionFactory, AppCacheRedisProperties appCacheRedisProperties, ObjectMapper objectMapper) {
        // --- Global defaults ---
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(appCacheRedisProperties.getDefaults().getTtl())
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(resolveSerializer(appCacheRedisProperties.getDefaults().getValueSerializer(), objectMapper)));

        // --- Per-cache overrides ---
        Map<String, RedisCacheConfiguration> cacheConfigs = appCacheRedisProperties.getCaches().stream()
                .collect(Collectors.toMap(
                        AppCacheRedisProperties.CacheSpecProperties::getName,
                        spec -> {
                            RedisCacheConfiguration cfg = defaultConfig;
                            if (spec.getTtl() != null) {
                                cfg = cfg.entryTtl(spec.getTtl());
                            }
                            if (spec.getValueSerializer() != null) {
                                cfg = cfg.serializeValuesWith(RedisSerializationContext.SerializationPair
                                        .fromSerializer(resolveSerializer(spec.getValueSerializer(), objectMapper)));
                            }
                            return cfg;
                        }
                ));

        RedisCacheManager.RedisCacheManagerBuilder redisCacheManagerBuilder = RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigs);
        if(Boolean.TRUE.equals(appCacheRedisProperties.getTrasactionAware())) {
            redisCacheManagerBuilder.transactionAware();
        }
        return redisCacheManagerBuilder
                .build();
    }

    private RedisSerializer<?> resolveSerializer(RedisSerializerType type, ObjectMapper objectMapper) {
        return switch (type) {
            case STRING -> new StringRedisSerializer();
            case JSON -> new GenericJackson2JsonRedisSerializer(objectMapper);
            case JDK -> new JdkSerializationRedisSerializer();
        };
    }
}
