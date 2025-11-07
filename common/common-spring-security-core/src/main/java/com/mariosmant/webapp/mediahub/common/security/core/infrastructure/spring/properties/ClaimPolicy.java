package com.mariosmant.webapp.mediahub.common.security.core.infrastructure.spring.properties;

import java.time.Duration;
import java.util.Set;

public final class ClaimPolicy {
    private final Set<String> requiredAudiences;
    private final Set<String> allowedAzp;
    private final Set<String> requiredScopes;
    private final String subjectPattern;
    private final Duration clockSkew;
    private final Duration jtiCacheTtl;

    public ClaimPolicy(Set<String> requiredAudiences,
                       Set<String> allowedAzp,
                       Set<String> requiredScopes,
                       String subjectPattern,
                       Duration clockSkew,
                       Duration jtiCacheTtl) {
        this.requiredAudiences = requiredAudiences == null ? Set.of() : Set.copyOf(requiredAudiences);
        this.allowedAzp = allowedAzp == null ? Set.of() : Set.copyOf(allowedAzp);
        this.requiredScopes = requiredScopes == null ? Set.of() : Set.copyOf(requiredScopes);
        this.subjectPattern = subjectPattern;
        this.clockSkew = clockSkew;
        this.jtiCacheTtl = jtiCacheTtl;
    }

    public Set<String> requiredAudiences() { return requiredAudiences; }
    public Set<String> allowedAzp() { return allowedAzp; }
    public Set<String> requiredScopes() { return requiredScopes; }
    public String subjectPattern() { return subjectPattern; }
    public Duration clockSkew() { return clockSkew; }
    public Duration jtiCacheTtl() { return jtiCacheTtl; }
}
