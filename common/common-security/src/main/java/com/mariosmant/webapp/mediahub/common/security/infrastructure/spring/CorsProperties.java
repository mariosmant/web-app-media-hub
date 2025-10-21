package com.mariosmant.webapp.mediahub.common.security.infrastructure.spring;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Getter
@NoArgsConstructor
@ConfigurationProperties(prefix = "app.cors")
public class CorsProperties {
    private List<String> allowedOrigins = List.of("*");
    private List<String> allowedMethods = List.of("GET","POST","PUT","DELETE","OPTIONS");
    private List<String> allowedHeaders = List.of("*");
    private boolean allowCredentials = false;
}
