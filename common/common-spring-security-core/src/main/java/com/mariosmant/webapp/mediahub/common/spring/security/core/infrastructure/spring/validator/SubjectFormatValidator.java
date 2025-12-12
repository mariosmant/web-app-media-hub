package com.mariosmant.webapp.mediahub.common.spring.security.core.infrastructure.spring.validator;

import com.mariosmant.webapp.mediahub.common.spring.security.core.infrastructure.spring.properties.ClaimSubjectPolicy;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Set;
import java.util.regex.Pattern;

public final class SubjectFormatValidator implements OAuth2TokenValidator<Jwt> {
    private final ClaimSubjectPolicy claimSubjectPolicy;
    private final Pattern userSubjectPattern;
    private final Pattern serviceAccountSubjectPattern;
    private final Set<String> subjectAllowedClientIds;
    public SubjectFormatValidator(ClaimSubjectPolicy claimSubjectPolicy, Set<String> subjectAllowedClientIds) {
        this.claimSubjectPolicy = claimSubjectPolicy;
        if(claimSubjectPolicy.isSubjectIsUser()) {
            this.userSubjectPattern = claimSubjectPolicy.getUserSubjectPattern() != null ? Pattern.compile(claimSubjectPolicy.getUserSubjectPattern()) : Pattern.compile("^[0-9a-fA-F\\\\-]{36}$");
        }
        else {
            this.userSubjectPattern = null;
        }

        if(claimSubjectPolicy.isSubjectIsServiceAccount()) {
            this.serviceAccountSubjectPattern = claimSubjectPolicy.getServiceAccountSubjectPattern() != null ? Pattern.compile(claimSubjectPolicy.getServiceAccountSubjectPattern()) : null;
        }
        else {
            this.serviceAccountSubjectPattern = null;
        }
        this.subjectAllowedClientIds = subjectAllowedClientIds;
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt jwt) {
        String sub = jwt.getSubject();
        if(claimSubjectPolicy.isSubjectIsUser() && claimSubjectPolicy.isSubjectIsServiceAccount()) {
            if (sub != null && (subMatchesUserPattern(sub) || subMatchesServiceAccountsPatternOrClientIds(sub, subjectAllowedClientIds))) return OAuth2TokenValidatorResult.success();
        }
        else if(claimSubjectPolicy.isSubjectIsUser()) {
            if(subMatchesUserPattern(sub)) return OAuth2TokenValidatorResult.success();
        }
        else if(claimSubjectPolicy.isSubjectIsServiceAccount()) {
            if(subMatchesServiceAccountsPatternOrClientIds(sub, subjectAllowedClientIds)) return OAuth2TokenValidatorResult.success();
        }
        return OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token","Subject format invalid", null));
    }

    private boolean subMatchesUserPattern(String sub) {
        return userSubjectPattern != null && userSubjectPattern.matcher(sub).matches();
    }

    private boolean subMatchesServiceAccountsPatternOrClientIds(String sub, Set<String> subjectAllowedClientIds) {
        return (serviceAccountSubjectPattern != null && serviceAccountSubjectPattern.matcher(sub).matches()) ||
                subjectAllowedClientIds.contains(sub);
    }
}
