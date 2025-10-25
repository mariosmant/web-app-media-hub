package com.mariosmant.webapp.mediahub.common.security.core.infrastructure.spring.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class ValidatorPolicy {
    boolean issuer;
    boolean algorithm;
    boolean typHeader;
    boolean audience;
    boolean authorizedParty;
    boolean scope;
    boolean subjectFormat;
    boolean expNbfSkew;
    boolean kid;
    boolean tenant;
    boolean jti;
    private final AlgorithmPolicy algorithmPolicy;
    private final HeaderPolicy headerPolicy;
    private final ClaimPolicy claimPolicy;

    public AlgorithmPolicy algorithmPolicy() { return algorithmPolicy; }
    public HeaderPolicy headerPolicy() { return headerPolicy; }
    public ClaimPolicy claimPolicy() { return claimPolicy; }
}
