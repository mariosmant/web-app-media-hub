package com.mariosmant.webapp.mediahub.common.security.core.infrastructure.spring.properties;

import java.util.Set;

public final class HeaderPolicy {
    private final Set<String> allowedTypHeaders; // e.g., JWT
    private final boolean enforceKidPinning;
    private final Set<String> allowedKids;

    public HeaderPolicy(Set<String> allowedTypHeaders, boolean enforceKidPinning, Set<String> allowedKids) {
        this.allowedTypHeaders = allowedTypHeaders == null ? Set.of() : Set.copyOf(allowedTypHeaders);
        this.enforceKidPinning = enforceKidPinning;
        this.allowedKids = allowedKids == null ? Set.of() : Set.copyOf(allowedKids);
    }

    public Set<String> allowedTypHeaders() { return allowedTypHeaders; }
    public boolean enforceKidPinning() { return enforceKidPinning; }
    public Set<String> allowedKids() { return allowedKids; }
}
