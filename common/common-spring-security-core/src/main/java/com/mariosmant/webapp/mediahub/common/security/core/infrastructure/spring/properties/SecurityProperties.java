package com.mariosmant.webapp.mediahub.common.security.core.infrastructure.spring.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.jwt")
public final class SecurityProperties {
    private final String issuer;
    private final String jwkSetUri;
    private final ValidatorPolicy validatorPolicy;

    public SecurityProperties(String issuer,
                              String jwkSetUri,
                              ValidatorPolicy validatorPolicy) {
        this.issuer = issuer;
        this.jwkSetUri = jwkSetUri;
        this.validatorPolicy = validatorPolicy;
    }

    public String issuer() { return issuer; }
    public String jwkSetUri() { return jwkSetUri; }
    public ValidatorPolicy validatorPolicy() { return validatorPolicy; }
}
