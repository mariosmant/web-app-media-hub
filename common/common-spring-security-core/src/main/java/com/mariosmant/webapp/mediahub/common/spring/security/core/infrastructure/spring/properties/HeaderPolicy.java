package com.mariosmant.webapp.mediahub.common.spring.security.core.infrastructure.spring.properties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class HeaderPolicy {
    private Set<String> allowedTypHeaders; // e.g., JWT
    private boolean enforceKidPinning;
    private Set<String> allowedKids;
}
