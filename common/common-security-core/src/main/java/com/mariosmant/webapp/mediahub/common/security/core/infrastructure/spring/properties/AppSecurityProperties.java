package com.mariosmant.webapp.mediahub.common.security.core.infrastructure.spring.properties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ConfigurationProperties(prefix = "app.security")
public class AppSecurityProperties {
    private String issuerUri;
    private String jwkSetUri;
    private List<String> audiences;
    private String introspectionUri;
    private String introspectionKeystorePath;
    private String introspectionKeystorePassword;
    private String introspectionTruststorePath;
    private String introspectionTruststorePassword;
}
