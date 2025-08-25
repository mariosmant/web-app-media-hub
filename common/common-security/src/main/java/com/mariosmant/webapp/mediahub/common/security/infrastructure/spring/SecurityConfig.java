package com.mariosmant.webapp.mediahub.common.security.infrastructure.spring;

//import com.mariosmant.webapp.mediahub.common.rate.limiter.token.bucket.domain.TokenBucketRateLimiter;
import com.mariosmant.webapp.mediahub.common.security.domain.TokenValidator;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableConfigurationProperties({AppSecurityProperties.class, CorsProperties.class})
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           CorsProperties corsProps,
                                           AppSecurityProperties secProps) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration cfg = new CorsConfiguration();
                    cfg.setAllowedOrigins(corsProps.getAllowedOrigins());
                    cfg.setAllowedMethods(corsProps.getAllowedMethods());
                    cfg.setAllowedHeaders(corsProps.getAllowedHeaders());
                    cfg.setAllowCredentials(corsProps.isAllowCredentials());
                    return cfg;
                }))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(reg -> reg
                        .requestMatchers(HttpMethod.GET, "/actuator/health").permitAll()
                        .anyRequest().authenticated()
                )
//                .addFilterBefore(new TokenBucketRateLimiter(), BasicAuthenticationFilter.class)
                .oauth2ResourceServer(oauth -> oauth.jwt(Customizer.withDefaults()));
        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder(AppSecurityProperties secProps) {
        NimbusJwtDecoder decoder;
        if (secProps.getJwkSetUri() != null && !secProps.getJwkSetUri().isBlank()) {
            decoder = NimbusJwtDecoder.withJwkSetUri(secProps.getJwkSetUri()).build();
        } else if (secProps.getIssuerUri() != null && !secProps.getIssuerUri().isBlank()) {
            decoder = JwtDecoders.fromIssuerLocation(secProps.getIssuerUri());
        } else {
            throw new IllegalStateException("Either app.security.issuer-uri or app.security.jwk-set-uri must be configured");
        }
        if (secProps.getAudiences() != null && !secProps.getAudiences().isEmpty()) {
            decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
                    JwtValidators.createDefaultWithIssuer(secProps.getIssuerUri()),
                    new TokenValidator(secProps.getAudiences())
            ));
        }
        return decoder;
    }
}
