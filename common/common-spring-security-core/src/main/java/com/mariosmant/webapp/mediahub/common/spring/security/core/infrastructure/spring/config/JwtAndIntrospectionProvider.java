package com.mariosmant.webapp.mediahub.common.spring.security.core.infrastructure.spring.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;

public class JwtAndIntrospectionProvider implements AuthenticationProvider {
    private final JwtAuthenticationProvider jwtProvider;
    private final OpaqueTokenIntrospector introspector;

    public JwtAndIntrospectionProvider(JwtDecoder jwtDecoder,
                                       OpaqueTokenIntrospector introspector,
                                       Converter<Jwt, ? extends AbstractAuthenticationToken> jwtConverter) {
        this.jwtProvider = new JwtAuthenticationProvider(jwtDecoder);
        if (jwtConverter != null) {
            this.jwtProvider.setJwtAuthenticationConverter(jwtConverter);
        }
        this.introspector = introspector;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // Step 1: Local JWT validation
        Authentication jwtAuth = jwtProvider.authenticate(authentication);

        // Step 2: Introspection call (revocation / activity check)
        String token = ((BearerTokenAuthenticationToken) authentication).getToken();
        OAuth2AuthenticatedPrincipal principal = introspector.introspect(token);

        Boolean active = principal.getAttribute("active");
        if (active == null || !active) {
            throw new BadCredentialsException("Token is not active (revoked/expired)");
        }

        // Optionally enforce scope/audience from introspection
        // String scope = principal.getAttribute("scope");
        // List<String> aud = principal.getAttribute("aud");

        return jwtAuth; // return the JWT-based Authentication after introspection passes
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return BearerTokenAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
