package com.mariosmant.webapp.mediahub.common.spring.security.core.infrastructure.spring.properties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class ClaimPolicy {
    private Set<String> requiredAudiences = new HashSet<>();
    private Set<String> allowedAzp = new HashSet<>();
    private Set<String> requiredScopes = new HashSet<>();
    private ClaimSubjectPolicy claimSubjectPolicy = new ClaimSubjectPolicy();
    private Duration clockSkew;
    private Duration jtiCacheTtl;
}
