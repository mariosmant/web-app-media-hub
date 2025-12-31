package com.mariosmant.webapp.mediahub.user.service.infrastructure.spring.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;

@Configuration
public class SecurityConfig {
    @Bean
    public OAuth2RestTemplate oAuth2RestTemplate(
            OAuth2ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientService authorizedClientService
    ) {
        // Creating OAuth2RestTemplate that can handle OAuth2 communication
        return new OAuth2RestTemplate(clientRegistrationRepository, authorizedClientService);
    }
}
