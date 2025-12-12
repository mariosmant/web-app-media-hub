package com.mariosmant.webapp.mediahub.common.spring.security.core.infrastructure.spring.config;

//import domain.bucket.token.limiter.rate.com.mariosmant.webapp.mediahub.common.web.TokenBucketRateLimiter;
import com.mariosmant.webapp.mediahub.common.spring.security.core.infrastructure.spring.properties.AppSecurityProperties;
import com.mariosmant.webapp.mediahub.common.spring.security.core.infrastructure.spring.properties.CorsProperties;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.*;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.authentication.OpaqueTokenAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

@AutoConfiguration
@EnableConfigurationProperties({ AppSecurityProperties.class, CorsProperties.class})
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                           CorsProperties corsProps,
                                                   AuthenticationManagerResolver<HttpServletRequest> resolver) throws Exception {
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
                .headers(h -> h
                        .contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'none'; frame-ancestors 'none'; base-uri 'none'"))
                        .httpStrictTransportSecurity(hsts -> hsts.includeSubDomains(true).preload(true))
                        //.xssProtection(xss -> xss.block(true)) // TODO
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::deny)
                )

                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(reg -> reg
                        .requestMatchers(HttpMethod.GET, "/actuator/health").permitAll()
                        .anyRequest().authenticated()
                )
//                .addFilterBefore(new TokenBucketRateLimiter(), BasicAuthenticationFilter.class)
                //.oauth2ResourceServer(oauth -> oauth.jwt(Customizer.withDefaults()));
//                .oauth2ResourceServer(oauth -> {
//                            OpaqueTokenIntrospector introspector = introspectorProvider.getIfAvailable();
//                            switch (props.getAuthMode()) {
//                                case "jwt" -> oauth.jwt(jwt -> jwt.decoder(jwtDecoder));
//                                case "introspection" -> {
//                                    nullSafeCheckIntrospector(introspector);
//                                    oauth.opaqueToken(opaque -> opaque.introspector(introspector)); // TODO null checks.
//                                }
//                                case "auto" -> oauth.authenticationManagerResolver(request -> {
//                                    String token = extractToken(request);
//                                    if (isJwt(token)) {
//                                        return new ProviderManager(new JwtAuthenticationProvider(jwtDecoder));
//                                    } else {
//                                        nullSafeCheckIntrospector(introspector);
//                                        return new ProviderManager(new OpaqueTokenAuthenticationProvider(introspector));
//                                    }
//                                });
//                                case "both" -> oauth.authenticationManagerResolver(request -> {
//                                    String token = extractToken(request);
//                                    nullSafeCheckIntrospector(introspector);
//
//                                    if (isJwt(token)) {
//                                        return new ProviderManager(new JwtAndIntrospectionProvider(jwtDecoder, introspector, null));
//                                    } else {
//                                        // Opaque path: introspection only
//                                        return new ProviderManager(new OpaqueTokenAuthenticationProvider(introspector));
//                                    }
//                                });
//
//                                default -> throw new IllegalStateException("Unknown auth mode: " + props.getAuthMode());
//                            }});
                .oauth2ResourceServer(oauth -> oauth.authenticationManagerResolver(resolver));
        return http.build();
    }

    private void nullSafeCheckIntrospector(OpaqueTokenIntrospector introspector) throws IllegalStateException {
        if(introspector == null) {
            throw new IllegalStateException("Could not find introspector");
        }
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        return (header != null && header.startsWith("Bearer ")) ? header.substring(7) : null;
    }

    private boolean isJwt(String token) {
        // Simple heuristic: JWTs have 3 dot-separated parts
        return token != null && token.split("\\.").length == 3;
    }

    @Bean
    public AuthenticationManagerResolver<HttpServletRequest> authenticationManagerResolver(
            AppSecurityProperties props,
            JwtDecoder jwtDecoder,
            ObjectProvider<OpaqueTokenIntrospector> introspectorProvider) {

        OpaqueTokenIntrospector introspector = introspectorProvider.getIfAvailable();

        return request -> {
            String token = extractToken(request);

            switch (props.getAuthMode()) {
                case "jwt":
                    return new ProviderManager(new JwtAuthenticationProvider(jwtDecoder));

                case "introspection":
                    nullSafeCheckIntrospector(introspector);
                    return new ProviderManager(new OpaqueTokenAuthenticationProvider(introspector));

                case "auto":
                    if (isJwt(token)) {
                        return new ProviderManager(new JwtAuthenticationProvider(jwtDecoder));
                    } else {
                        nullSafeCheckIntrospector(introspector);
                        return new ProviderManager(new OpaqueTokenAuthenticationProvider(introspector));
                    }

                case "both":
                    nullSafeCheckIntrospector(introspector);
                    if (isJwt(token)) {
                        return new ProviderManager(new JwtAndIntrospectionProvider(jwtDecoder, introspector, null));
                    } else {
                        return new ProviderManager(new OpaqueTokenAuthenticationProvider(introspector));
                    }

                default:
                    throw new IllegalStateException("Unknown auth mode: " + props.getAuthMode());
            }
        };
    }


//    @Bean
//    AuthenticationManager authenticationManager(JwtDecoder jwtDecoder,
//                                                SpringOpaqueTokenIntrospector opaqueIntrospector,
//                                                AppSecurityProperties secProps) {
//        // Provider 1: local JWT validation
//        JwtAuthenticationProvider jwtProvider = new JwtAuthenticationProvider(jwtDecoder);
//
//        // Provider 2: introspection
//        OpaqueTokenAuthenticationProvider opaqueProvider =
//                new OpaqueTokenAuthenticationProvider(opaqueIntrospector);
//
//        // Try JWT first, then introspection
//        return new ProviderManager(List.of(jwtProvider
//        //        , opaqueProvider
//        ));
//    }


//    @Bean
//    public JwtDecoder jwtDecoder(AppSecurityProperties secProps) {
//        NimbusJwtDecoder decoder;
//        if (secProps.getJwkSetUri() != null && !secProps.getJwkSetUri().isBlank()) {
//            // In case JWK Set URI is provided, use that explicitly.
//            decoder = NimbusJwtDecoder.withJwkSetUri(secProps.getJwkSetUri()).build();
//        } else if (secProps.getIssuerUri() != null && !secProps.getIssuerUri().isBlank()) {
//            // Else if Issuer URI is provided, use that to auto-discover based on well-known endpoint.
//            decoder = JwtDecoders.fromIssuerLocation(secProps.getIssuerUri());
//        } else {
//            throw new IllegalStateException("Either app.security.issuer-uri or app.security.jwk-set-uri must be configured");
//        }
//        if (secProps.getAudiences() != null && !secProps.getAudiences().isEmpty()) {
//            decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
//                    JwtValidators.createDefaultWithIssuer(secProps.getIssuerUri()),
//                    new TokenValidator(secProps.getAudiences())
//            ));
//        }
//        return decoder;
//    }
//
//    @Bean
//    SpringOpaqueTokenIntrospector opaqueTokenIntrospector(AppSecurityProperties secProps) throws Exception {
//        String introspectionUri = secProps.getIntrospectionUri();
//
//        // --- Load client keystore (with private key + client cert) ---
//        KeyStore keyStore = KeyStore.getInstance("PKCS12");
//        try (FileInputStream fis = new FileInputStream(secProps.getIntrospectionKeystorePath())) {
//            keyStore.load(fis, secProps.getIntrospectionKeystorePassword().toCharArray());
//        }
//
//        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
//        kmf.init(keyStore, secProps.getIntrospectionKeystorePassword().toCharArray());
//
//        // --- Load truststore (with Keycloak server cert or CA) ---
//        KeyStore trustStore = KeyStore.getInstance("PKCS12");
//        try (FileInputStream fis = new FileInputStream(secProps.getIntrospectionTruststorePath())) {
//            trustStore.load(fis, secProps.getIntrospectionTruststorePassword().toCharArray());
//        }
//
//        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
//        tmf.init(trustStore);
//
//        // --- Build SSLContext (TLS 1.3 if supported) ---
//        SSLContext sslContext = SSLContext.getInstance("TLSv1.3");
//        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
//
//        // --- RestTemplate with custom SSLContext ---
//        RestTemplate restTemplate = new RestTemplate(new SimpleClientHttpRequestFactory() {
//            @Override
//            protected void prepareConnection(@NonNull HttpURLConnection connection, @NonNull String httpMethod) throws IOException {
//                if (connection instanceof HttpsURLConnection https) {
//                    https.setSSLSocketFactory(sslContext.getSocketFactory());
//                }
//                super.prepareConnection(connection, httpMethod);
//            }
//        });
//
//
//        return new SpringOpaqueTokenIntrospector(introspectionUri, restTemplate);
//    }
}
