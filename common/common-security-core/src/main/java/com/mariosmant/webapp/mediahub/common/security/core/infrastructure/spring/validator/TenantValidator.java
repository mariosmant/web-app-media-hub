package com.mariosmant.webapp.mediahub.common.security.core.infrastructure.spring.validator;

import com.mariosmant.webapp.mediahub.common.security.core.domain.claims.TenantContext;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.jwt.Jwt;

public final class TenantValidator implements OAuth2TokenValidator<Jwt> {
    @Override
    public OAuth2TokenValidatorResult validate(Jwt jwt) {
        String tokenTenant = (String) jwt.getClaims().get("tenant_id");
        String requestTenant = TenantContext.get();
        if (tokenTenant != null && tokenTenant.equals(requestTenant)) {
            return OAuth2TokenValidatorResult.success();
        }
        return OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token","Tenant mismatch", null));
    }
}
