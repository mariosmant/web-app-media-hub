package com.mariosmant.webapp.mediahub.common.spring.security.core.application.security;

import com.mariosmant.webapp.mediahub.common.spring.security.core.infrastructure.spring.properties.AppSecurityProperties;
import org.springframework.stereotype.Component;

@Component
public final class JwtSecurityOrchestrator {
    private final AppSecurityProperties props;

    public JwtSecurityOrchestrator(AppSecurityProperties props) {
        this.props = props;
    }

    public AppSecurityProperties properties() {
        return props;
    }
}
