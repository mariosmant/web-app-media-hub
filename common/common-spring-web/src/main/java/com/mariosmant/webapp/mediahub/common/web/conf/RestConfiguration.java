package com.mariosmant.webapp.mediahub.common.web.conf;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import java.time.Duration;

@AutoConfiguration
@ConditionalOnProperty(
        prefix = "common.configuration.enabled",
        name = "rest",
        havingValue = "true",
        matchIfMissing = true
)
public class RestConfiguration {

    @Bean
    public RestTemplate restTemplate() {

        // Spring Boot 4.x requires manual configuration
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout((int) Duration.ofSeconds(60).toMillis());
        factory.setReadTimeout((int) Duration.ofSeconds(60).toMillis());

        // RestTemplate restTemplate = new RestTemplate(factory);
        // Add interceptors, message converters, error handlers if needed
        // restTemplate.getInterceptors().add(...);

        return new RestTemplate(factory);
    }
}
