package com.mariosmant.webapp.mediahub.common.spring.security.core.infrastructure.spring.properties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@NoArgsConstructor
@ConfigurationProperties(prefix = "security.jwt")
public class AppSecurityProperties {
    private String issuer;
    private String jwkSetUri;
    private String authMode;
    private ValidatorPolicy validatorPolicy = new ValidatorPolicy();
}
