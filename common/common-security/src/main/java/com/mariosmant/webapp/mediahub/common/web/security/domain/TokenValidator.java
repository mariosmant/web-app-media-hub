package com.mariosmant.webapp.mediahub.common.web.security.domain;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;

import java.util.List;

public class TokenValidator implements OAuth2TokenValidator<Jwt> {
    private final List<String> allowedAudiences;
    private final OAuth2Error error = new OAuth2Error("invalid_token", "The required audience is missing", null);

    public TokenValidator(List<String> allowedAudiences) {
        this.allowedAudiences = allowedAudiences;
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt token) {
        if (allowedAudiences == null || allowedAudiences.isEmpty()) {
            return OAuth2TokenValidatorResult.success();
        }
        List<String> aud = token.getAudience();
        boolean ok = aud != null && aud.stream().anyMatch(allowedAudiences::contains);
        return ok ? OAuth2TokenValidatorResult.success() : OAuth2TokenValidatorResult.failure(error);
    }
}
