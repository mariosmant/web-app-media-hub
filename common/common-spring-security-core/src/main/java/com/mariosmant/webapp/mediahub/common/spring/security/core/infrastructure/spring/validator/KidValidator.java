package com.mariosmant.webapp.mediahub.common.spring.security.core.infrastructure.spring.validator;

import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.jwt.Jwt;
import java.util.Set;

public final class KidValidator implements OAuth2TokenValidator<Jwt> {
    private final boolean enforce;
    private final Set<String> allowedKids;
    public KidValidator(boolean enforce, Set<String> allowedKids) {
        this.enforce = enforce;
        this.allowedKids = allowedKids == null ? Set.of() : Set.copyOf(allowedKids);
    }
    @Override
    public OAuth2TokenValidatorResult validate(Jwt jwt) {
        if (!enforce) return OAuth2TokenValidatorResult.success();
        String kid = (String) jwt.getHeaders().get("kid");
        if (kid != null && allowedKids.contains(kid)) return OAuth2TokenValidatorResult.success();
        return OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token","Unpinned or missing kid", null));
    }
}
