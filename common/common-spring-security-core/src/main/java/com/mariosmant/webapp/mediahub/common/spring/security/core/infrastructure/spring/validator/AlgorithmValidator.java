package com.mariosmant.webapp.mediahub.common.spring.security.core.infrastructure.spring.validator;

import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Set;

public final class AlgorithmValidator implements OAuth2TokenValidator<Jwt> {
    private final Set<String> allowedAlgs;
    public AlgorithmValidator(Set<String> allowedAlgs) { this.allowedAlgs = Set.copyOf(allowedAlgs); }
    @Override
    public OAuth2TokenValidatorResult validate(Jwt jwt) {
        String alg = (String) jwt.getHeaders().get("alg");
        if (alg != null && allowedAlgs.contains(alg)) return OAuth2TokenValidatorResult.success();
        return OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token","Disallowed JOSE algorithm", null));
    }
}

