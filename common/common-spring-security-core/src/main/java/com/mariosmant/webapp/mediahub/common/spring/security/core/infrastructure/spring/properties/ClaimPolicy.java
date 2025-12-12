package com.mariosmant.webapp.mediahub.common.spring.security.core.infrastructure.spring.properties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class ClaimPolicy {
    private Set<String> requiredAudiences;
    private Set<String> allowedAzp;
    private Set<String> requiredScopes;
    private ClaimSubjectPolicy claimSubjectPolicy;
    private Duration clockSkew;
    private Duration jtiCacheTtl;
}
