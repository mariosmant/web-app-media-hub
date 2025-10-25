package com.mariosmant.webapp.mediahub.common.security.core.infrastructure.spring.validator;

import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.jwt.Jwt;
import java.util.Set;

public final class TypHeaderValidator implements OAuth2TokenValidator<Jwt> {
    private final Set<String> allowedTypes;
    public TypHeaderValidator(Set<String> allowedTypes) { this.allowedTypes = Set.copyOf(allowedTypes); }
    @Override
    public OAuth2TokenValidatorResult validate(Jwt jwt) {
        String typ = (String) jwt.getHeaders().get("typ");
        if (typ != null && allowedTypes.contains(typ)) return OAuth2TokenValidatorResult.success();
        return OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token","Invalid typ header", null));
    }
}
