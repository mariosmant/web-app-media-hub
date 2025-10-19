package com.mariosmant.webapp.mediahub.common.web.conf;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Configuration
@ConditionalOnProperty(prefix = "common.configuration.enabled", name = "rest", havingValue = "true", matchIfMissing = true)
public class RestConfiguration {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        // Do any additional configuration here
        builder.connectTimeout(Duration.of(60L, ChronoUnit.SECONDS));
        builder.readTimeout(Duration.of(60L, ChronoUnit.SECONDS));
        return builder.build();
    }
}
