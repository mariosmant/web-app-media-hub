package com.mariosmant.webapp.mediahub.common.security.core.application.security;

import com.mariosmant.webapp.mediahub.common.security.core.infrastructure.spring.properties.SecurityProperties;
import org.springframework.stereotype.Component;

@Component
public final class JwtSecurityOrchestrator {
    private final SecurityProperties props;

    public JwtSecurityOrchestrator(SecurityProperties props) {
        this.props = props;
    }

    public SecurityProperties properties() {
        return props;
    }
}
