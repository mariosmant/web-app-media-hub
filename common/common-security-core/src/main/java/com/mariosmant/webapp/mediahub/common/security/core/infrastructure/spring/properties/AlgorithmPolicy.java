package com.mariosmant.webapp.mediahub.common.security.core.infrastructure.spring.properties;

import java.util.Set;

public final class AlgorithmPolicy {
    private final Set<String> allowedAlgs; // e.g., PS256

    public AlgorithmPolicy(Set<String> allowedAlgs) {
        this.allowedAlgs = allowedAlgs == null ? Set.of() : Set.copyOf(allowedAlgs);
    }

    public Set<String> allowedAlgs() { return allowedAlgs; }
}

