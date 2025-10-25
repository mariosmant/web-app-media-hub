package com.mariosmant.webapp.mediahub.common.security.core.infrastructure.spring.validator;

import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;

import java.time.Duration;

public final class ExpNbfSkewValidator implements OAuth2TokenValidator<Jwt> {
    private final JwtTimestampValidator delegate;

    public ExpNbfSkewValidator(Duration skew) {
        this.delegate = new JwtTimestampValidator(skew);
    }
    @Override
    public OAuth2TokenValidatorResult validate(Jwt jwt) {
        return delegate.validate(jwt);
    }
}
