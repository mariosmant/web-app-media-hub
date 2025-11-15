package com.mariosmant.webapp.mediahub.common.spring.security.core.infrastructure.spring.validator;

import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.jwt.Jwt;
import java.util.regex.Pattern;

public final class SubjectFormatValidator implements OAuth2TokenValidator<Jwt> {
    private final Pattern pattern;
    public SubjectFormatValidator(String regex) { this.pattern = Pattern.compile(regex); }
    @Override
    public OAuth2TokenValidatorResult validate(Jwt jwt) {
        String sub = jwt.getSubject();
        if (sub != null && pattern.matcher(sub).matches()) return OAuth2TokenValidatorResult.success();
        return OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token","Subject format invalid", null));
    }
}
