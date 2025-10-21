package com.mariosmant.webapp.mediahub.common.security.infrastructure.spring;

//import domain.bucket.token.limiter.rate.com.mariosmant.webapp.mediahub.common.web.TokenBucketRateLimiter;
import com.mariosmant.webapp.mediahub.common.security.domain.TokenValidator;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.authentication.OpaqueTokenAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.introspection.SpringOpaqueTokenIntrospector;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.security.KeyStore;
import java.util.List;

@AutoConfiguration
@EnableConfigurationProperties({AppSecurityProperties.class, CorsProperties.class})
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           CorsProperties corsProps,
                                           AuthenticationManager authenticationManager) throws Exception {
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
//                .oauth2ResourceServer(oauth -> oauth.jwt(Customizer.withDefaults()));
                .oauth2ResourceServer(oauth -> oauth
                        .authenticationManagerResolver(request -> authenticationManager));
        return http.build();
    }

    @Bean
    AuthenticationManager authenticationManager(JwtDecoder jwtDecoder,
                                                SpringOpaqueTokenIntrospector opaqueIntrospector,
                                                AppSecurityProperties secProps) {
        // Provider 1: local JWT validation
        JwtAuthenticationProvider jwtProvider = new JwtAuthenticationProvider(jwtDecoder);

        // Provider 2: introspection
        OpaqueTokenAuthenticationProvider opaqueProvider =
                new OpaqueTokenAuthenticationProvider(opaqueIntrospector);

        // Try JWT first, then introspection
        return new ProviderManager(List.of(jwtProvider
        //        , opaqueProvider
        ));
    }


    @Bean
    public JwtDecoder jwtDecoder(AppSecurityProperties secProps) {
        NimbusJwtDecoder decoder;
        if (secProps.getJwkSetUri() != null && !secProps.getJwkSetUri().isBlank()) {
            // In case JWK Set URI is provided, use that explicitly.
            decoder = NimbusJwtDecoder.withJwkSetUri(secProps.getJwkSetUri()).build();
        } else if (secProps.getIssuerUri() != null && !secProps.getIssuerUri().isBlank()) {
            // Else if Issuer URI is provided, use that to auto-discover based on well-known endpoint.
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

    @Bean
    SpringOpaqueTokenIntrospector opaqueTokenIntrospector(AppSecurityProperties secProps) throws Exception {
        String introspectionUri = secProps.getIntrospectionUri();

        // --- Load client keystore (with private key + client cert) ---
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        try (FileInputStream fis = new FileInputStream(secProps.getIntrospectionKeystorePath())) {
            keyStore.load(fis, secProps.getIntrospectionKeystorePassword().toCharArray());
        }

        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, secProps.getIntrospectionKeystorePassword().toCharArray());

        // --- Load truststore (with Keycloak server cert or CA) ---
        KeyStore trustStore = KeyStore.getInstance("PKCS12");
        try (FileInputStream fis = new FileInputStream(secProps.getIntrospectionTruststorePath())) {
            trustStore.load(fis, secProps.getIntrospectionTruststorePassword().toCharArray());
        }

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(trustStore);

        // --- Build SSLContext (TLS 1.3 if supported) ---
        SSLContext sslContext = SSLContext.getInstance("TLSv1.3");
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

        // --- RestTemplate with custom SSLContext ---
        RestTemplate restTemplate = new RestTemplate(new SimpleClientHttpRequestFactory() {
            @Override
            protected void prepareConnection(@NonNull HttpURLConnection connection, @NonNull String httpMethod) throws IOException {
                if (connection instanceof HttpsURLConnection https) {
                    https.setSSLSocketFactory(sslContext.getSocketFactory());
                }
                super.prepareConnection(connection, httpMethod);
            }
        });


        return new SpringOpaqueTokenIntrospector(introspectionUri, restTemplate);
    }
}
