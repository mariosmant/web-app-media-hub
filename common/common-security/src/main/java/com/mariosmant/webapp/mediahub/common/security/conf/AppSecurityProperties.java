package com.mariosmant.webapp.mediahub.common.security.conf;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Getter
//@Setter
@NoArgsConstructor
@ConfigurationProperties(prefix = "app.security")
public class AppSecurityProperties {
    private String issuerUri;
    private String jwkSetUri;
    private List<String> audiences;
}
