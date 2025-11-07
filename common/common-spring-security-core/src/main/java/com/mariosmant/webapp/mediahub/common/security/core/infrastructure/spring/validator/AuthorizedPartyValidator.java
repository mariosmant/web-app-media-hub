package com.mariosmant.webapp.mediahub.common.security.core.infrastructure.spring.validator;

import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.jwt.Jwt;
import java.util.Set;

public final class AuthorizedPartyValidator implements OAuth2TokenValidator<Jwt> {
    private final Set<String> allowedAzp;
    public AuthorizedPartyValidator(Set<String> allowedAzp) { this.allowedAzp = Set.copyOf(allowedAzp); }
    @Override
    public OAuth2TokenValidatorResult validate(Jwt jwt) {
        String azp = (String) jwt.getClaims().get("azp");
        if (azp != null && allowedAzp.contains(azp)) return OAuth2TokenValidatorResult.success();
        return OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token","Unauthorized azp", null));
    }
}
