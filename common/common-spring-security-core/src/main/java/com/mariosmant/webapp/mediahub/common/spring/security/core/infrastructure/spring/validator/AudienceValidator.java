package com.mariosmant.webapp.mediahub.common.spring.security.core.infrastructure.spring.validator;

import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.HashSet;
import java.util.Set;

public final class AudienceValidator implements OAuth2TokenValidator<Jwt> {
    private final Set<String> requiredAudiences;
    public AudienceValidator(Set<String> requiredAudiences) { this.requiredAudiences = Set.copyOf(requiredAudiences); }
    @Override
    public OAuth2TokenValidatorResult validate(Jwt jwt) {
        if (new HashSet<>(jwt.getAudience()).containsAll(requiredAudiences)) return OAuth2TokenValidatorResult.success();
        return OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token","Missing required audience(s)", null));
    }
}

