package com.mariosmant.webapp.mediahub.common.spring.security.core.infrastructure.spring.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ValidatorPolicy {
    private boolean issuer;
    private boolean algorithm;
    private boolean typHeader;
    private boolean audience;
    private boolean authorizedParty;
    private boolean scope;
    private boolean expNbfSkew;
    private boolean kid;
    private boolean tenant;
    private boolean jti;
    private AlgorithmPolicy algorithmPolicy = new AlgorithmPolicy();;
    private HeaderPolicy headerPolicy = new HeaderPolicy();
    private ClaimPolicy claimPolicy = new ClaimPolicy();
}
