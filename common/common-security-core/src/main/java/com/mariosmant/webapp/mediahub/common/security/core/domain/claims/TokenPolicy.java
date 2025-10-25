package com.mariosmant.webapp.mediahub.common.security.core.domain.claims;

import java.util.Set;

public record TokenPolicy(
        String issuer,
        Set<String> requiredAudiences,
        Set<String> allowedAzp,
        Set<String> requiredScopes
) {}
