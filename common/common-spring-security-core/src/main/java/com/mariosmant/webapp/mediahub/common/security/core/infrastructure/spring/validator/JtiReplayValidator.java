package com.mariosmant.webapp.mediahub.common.security.core.infrastructure.spring.validator;

import com.mariosmant.webapp.mediahub.common.security.context.domain.service.JtiStore;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.jwt.Jwt;

public final class JtiReplayValidator implements OAuth2TokenValidator<Jwt> {

    private final JtiStore jtiStore;

    public JtiReplayValidator(JtiStore jtiStore) {
        this.jtiStore = jtiStore;
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt jwt) {
        String jti = (String) jwt.getClaims().get("jti");
        if (jti == null) {
            return OAuth2TokenValidatorResult.failure(
                    new OAuth2Error("invalid_token", "Missing jti", null)
            );
        }

        if (jtiStore.seen(jti)) {
            return OAuth2TokenValidatorResult.failure(
                    new OAuth2Error("invalid_token", "Replay detected", null)
            );
        }

        jtiStore.remember(jti);
        return OAuth2TokenValidatorResult.success();
    }
}
