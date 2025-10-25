package com.mariosmant.webapp.mediahub.common.security.core.infrastructure.spring.validator;

import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;
import java.util.Objects;

public final class CompositeJwtValidator implements OAuth2TokenValidator<Jwt> {
    private final List<OAuth2TokenValidator<Jwt>> validators;

    public CompositeJwtValidator(List<OAuth2TokenValidator<Jwt>> validators) {
        this.validators = List.copyOf(Objects.requireNonNull(validators, "validators"));
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt jwt) {
        for (OAuth2TokenValidator<Jwt> v : validators) {
            OAuth2TokenValidatorResult r = v.validate(jwt);
            if (r.hasErrors()) { // TODO
                return r; // fail fast
            }
        }
        return OAuth2TokenValidatorResult.success();
    }
}
