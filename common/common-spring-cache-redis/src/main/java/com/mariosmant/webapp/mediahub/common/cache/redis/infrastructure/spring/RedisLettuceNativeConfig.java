package com.mariosmant.webapp.mediahub.common.cache.redis.infrastructure.spring;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.TimeoutOptions;
import io.lettuce.core.api.StatefulConnection;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;

@AutoConfiguration
@EnableConfigurationProperties({AppCacheRedisProperties.class})
public class RedisLettuceNativeConfig {
    private static final String PORT_DELIMITER = ":";

    @Bean("txRedisConnectionFactory")
    public RedisConnectionFactory txRedisConnectionFactory(AppCacheRedisProperties appCacheRedisProperties) {
        return commonRedisConnectionFactory(appCacheRedisProperties, true);
    }

    @Bean("redisConnectionFactory")
    @Primary
    public RedisConnectionFactory redisConnectionFactory(AppCacheRedisProperties appCacheRedisProperties) {
        return commonRedisConnectionFactory(appCacheRedisProperties, false);
    }

    private RedisConnectionFactory commonRedisConnectionFactory(AppCacheRedisProperties appCacheRedisProperties, boolean isForTransactions) {
        AppCacheRedisProperties.LettuceSpecProperties lettuceSpecProperties = appCacheRedisProperties.getLettuce();

        AppCacheRedisProperties.LettucePoolSpecProperties poolSpecProperties = isForTransactions ? lettuceSpecProperties.getTransactionsPool() : lettuceSpecProperties.getPool();
        boolean isPoolingEnabled = poolSpecProperties != null && Boolean.TRUE.equals(poolSpecProperties.getEnabled());

        LettuceClientConfiguration.LettuceClientConfigurationBuilder clientBuilder;
        if(isPoolingEnabled) {
            // --- Pool configuration ---
            GenericObjectPoolConfig<StatefulConnection<?, ?>> poolConfig = new GenericObjectPoolConfig<>();
            poolConfig.setMaxTotal(poolSpecProperties.getMaxTotal());
            poolConfig.setMaxIdle(poolSpecProperties.getMaxIdle());
            poolConfig.setMinIdle(poolSpecProperties.getMinIdle());
            poolConfig.setMaxWait(poolSpecProperties.getMaxWait());

            clientBuilder = LettucePoolingClientConfiguration.builder()
                    .poolConfig(poolConfig);
        }
        else {
            clientBuilder = LettuceClientConfiguration.builder();
        }

        if(Boolean.TRUE.equals(lettuceSpecProperties.getUseSsl())) {
            clientBuilder.useSsl();
        }

        LettuceClientConfiguration client = clientBuilder
                .commandTimeout(lettuceSpecProperties.getCommandTimeout())
                .shutdownTimeout(lettuceSpecProperties.getShutdownTimeout()) // fast shutdown
                .clientOptions(ClientOptions.builder()
                        .autoReconnect(true)
                        .timeoutOptions(TimeoutOptions.builder()
                                // Fail fast if a connection cannot be established in time
                                .fixedTimeout(lettuceSpecProperties.getConnectTimeout())
                                .build())
                        .build())
                .build();

        LettuceConnectionFactory lettuceConnectionFactory;
        if (lettuceSpecProperties.isSentinel()) {
            RedisSentinelConfiguration sentinel = new RedisSentinelConfiguration();
            sentinel.master(lettuceSpecProperties.getSentinelMaster());
            if (lettuceSpecProperties.getSentinelNodes() != null) {
                for (String n : lettuceSpecProperties.getSentinelNodes()) {
                    String[] hp = n.split(PORT_DELIMITER);
                    sentinel.sentinel(hp[0], hp.length > 1 ? Integer.parseInt(hp[1]) : 26379);
                }
            }
            if (lettuceSpecProperties.hasPassword()) {
                sentinel.setPassword(RedisPassword.of(lettuceSpecProperties.getPassword()));
                lettuceSpecProperties.clearPassword();
            }
            lettuceConnectionFactory = new LettuceConnectionFactory(sentinel, client);
        } else {
            RedisStandaloneConfiguration standalone = new RedisStandaloneConfiguration(lettuceSpecProperties.getHost(), lettuceSpecProperties.getPort());
            if (lettuceSpecProperties.hasPassword()) {
                standalone.setPassword(RedisPassword.of(lettuceSpecProperties.getPassword()));
                lettuceSpecProperties.clearPassword();
            }
            lettuceConnectionFactory = new LettuceConnectionFactory(standalone, client);
        }

        lettuceConnectionFactory.setValidateConnection(true);
        lettuceConnectionFactory.setShareNativeConnection(!isPoolingEnabled); // When polling is enabled, make this false.
        return lettuceConnectionFactory;
    }
}
