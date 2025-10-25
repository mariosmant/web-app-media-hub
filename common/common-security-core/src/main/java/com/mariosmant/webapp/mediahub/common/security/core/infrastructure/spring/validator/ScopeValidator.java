package com.mariosmant.webapp.mediahub.common.security.core.infrastructure.spring.validator;

import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.jwt.Jwt;
import java.util.Set;

public final class ScopeValidator implements OAuth2TokenValidator<Jwt> {
    private final Set<String> requiredScopes;
    public ScopeValidator(Set<String> requiredScopes) { this.requiredScopes = Set.copyOf(requiredScopes); }
    @Override
    public OAuth2TokenValidatorResult validate(Jwt jwt) {
        Object scopeObj = jwt.getClaims().get("scope");
        Set<String> tokenScopes = switch (scopeObj) {
            case String s -> Set.of(s.split(" "));
            default -> Set.of();
        };
        if (tokenScopes.containsAll(requiredScopes)) return OAuth2TokenValidatorResult.success();
        return OAuth2TokenValidatorResult.failure(new OAuth2Error("insufficient_scope","Missing required scope(s)", null));
    }
}
