package com.mariosmant.webapp.mediahub.user.service.conf;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@Configuration
@EnableConfigurationProperties({AppRedisProperties.class})
public class RedisLettuceNativeConfig {

    @Bean
    public RedisConnectionFactory redisConnectionFactory(AppRedisProperties props) {
        LettuceClientConfiguration.LettuceClientConfigurationBuilder clientBuilder = LettuceClientConfiguration.builder();
        if(props.isSsl()) {
            clientBuilder.useSsl();
        }
        LettuceClientConfiguration client = clientBuilder.build();

        if (props.isSentinel()) {
            RedisSentinelConfiguration sentinel = new RedisSentinelConfiguration();
            sentinel.master(props.getSentinelMaster());
            if (props.getSentinelNodes() != null) {
                for (String n : props.getSentinelNodes()) {
                    String[] hp = n.split(":");
                    sentinel.sentinel(hp[0], hp.length > 1 ? Integer.parseInt(hp[1]) : 26379);
                }
            }
            if (props.hasPassword()) {
                sentinel.setPassword(RedisPassword.of(props.getPassword()));
            }
            return new LettuceConnectionFactory(sentinel, client);
        } else {
            RedisStandaloneConfiguration standalone = new RedisStandaloneConfiguration(props.getHost(), props.getPort());
            if (props.hasPassword()) {
                standalone.setPassword(RedisPassword.of(props.getPassword()));
            }
            return new LettuceConnectionFactory(standalone, client);
        }
    }
}
