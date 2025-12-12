package com.mariosmant.webapp.mediahub.common.spring.security.core.infrastructure.spring.config;

import com.mariosmant.webapp.mediahub.common.spring.security.context.domain.service.JtiStore;
import com.mariosmant.webapp.mediahub.common.spring.security.core.infrastructure.spring.properties.AppSecurityProperties;
import com.mariosmant.webapp.mediahub.common.spring.security.core.infrastructure.spring.properties.CorsProperties;
import com.mariosmant.webapp.mediahub.common.spring.security.core.infrastructure.spring.properties.ValidatorPolicy;
import com.mariosmant.webapp.mediahub.common.spring.security.core.infrastructure.spring.validator.*;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@AutoConfiguration
@EnableConfigurationProperties({ AppSecurityProperties.class })
public class JwtDecoderConfig {

    @Bean
    public NimbusJwtDecoder jwtDecoder(AppSecurityProperties props,
                                       OAuth2TokenValidator<Jwt> compositeValidator) {
        NimbusJwtDecoder decoder = NimbusJwtDecoder
                .withJwkSetUri(props.getJwkSetUri())
                // Algorithm pinning: RSA-PSS (PS256) only.
                .jwsAlgorithms(signatureAlgorithms ->
                        props.getValidatorPolicy().getAlgorithmPolicy().getAllowedAlgs().forEach(alg -> signatureAlgorithms.add(SignatureAlgorithm.valueOf(alg)))).build();
        decoder.setJwtValidator(compositeValidator);
        return decoder;
    }

    @Bean
    public OAuth2TokenValidator<Jwt> compositeValidator(AppSecurityProperties props,
                                                        ObjectProvider<JtiStore> jtiStoreProvider) {
        ValidatorPolicy validatorPolicy = props.getValidatorPolicy();

        List<OAuth2TokenValidator<Jwt>> validators = new ArrayList<>();

        addValidatorIf(validatorPolicy.isIssuer(),
                () -> JwtValidators.createDefaultWithIssuer(props.getIssuer()), validators);

        addValidatorIf(validatorPolicy.isAlgorithm(),
                () -> new AlgorithmValidator(validatorPolicy.getAlgorithmPolicy().getAllowedAlgs()), validators);

        addValidatorIf(validatorPolicy.isTypHeader(),
                () -> new TypHeaderValidator(validatorPolicy.getHeaderPolicy().getAllowedTypHeaders()), validators);

        addValidatorIf(validatorPolicy.isAudience(),
                () -> new AudienceValidator(validatorPolicy.getClaimPolicy().getRequiredAudiences()), validators);

        addValidatorIf(validatorPolicy.isAuthorizedParty(),
                () -> new AuthorizedPartyValidator(validatorPolicy.getClaimPolicy().getAllowedAzp()), validators);

        addValidatorIf(validatorPolicy.isScope(),
                () -> new ScopeValidator(validatorPolicy.getClaimPolicy().getRequiredScopes()), validators);

        boolean isUserSubject = validatorPolicy.getClaimPolicy().getClaimSubjectPolicy().isSubjectIsUser();
        boolean isServiceAccountSubject = validatorPolicy.getClaimPolicy().getClaimSubjectPolicy().isSubjectIsServiceAccount();
        addValidatorIf(isUserSubject || isServiceAccountSubject,
                () -> new SubjectFormatValidator(validatorPolicy.getClaimPolicy().getClaimSubjectPolicy(), validatorPolicy.getClaimPolicy().getClaimSubjectPolicy().getServiceAccountSubjectClientIds()), validators);

        addValidatorIf(validatorPolicy.isExpNbfSkew(),
                () -> new ExpNbfSkewValidator(validatorPolicy.getClaimPolicy().getClockSkew()), validators);

        addValidatorIf(validatorPolicy.isKid(),
                () -> new KidValidator(validatorPolicy.getHeaderPolicy().isEnforceKidPinning(),
                        validatorPolicy.getHeaderPolicy().getAllowedKids()), validators);

        addValidatorIf(validatorPolicy.isTenant(),
                TenantValidator::new, validators);

        if (validatorPolicy.isJti()) {
            jtiStoreProvider.orderedStream()
                    .forEach(store -> validators.add(new JtiReplayValidator(store)));
        }

        return new CompositeJwtValidator(validators);
    }

    private void addValidatorIf(boolean enabled,
                       Supplier<OAuth2TokenValidator<Jwt>> supplier,
                       List<OAuth2TokenValidator<Jwt>> validators) {
        if (enabled) {
            validators.add(supplier.get());
        }
    }

//    @Bean
//    public OAuth2TokenValidator<Jwt> compositeValidator(SecurityProperties props,
//                                                        ObjectProvider<JtiStore> jtiStoreProvider) {
//        ValidatorPolicy validatorPolicy = props.validatorPolicy();
//        OAuth2TokenValidator<Jwt> base = JwtValidators.createDefaultWithIssuer(props.issuer());
//        List<OAuth2TokenValidator<Jwt>> validators = new ArrayList<>(List.of(
//                base,
//                new AlgorithmValidator(validatorPolicy.algorithmPolicy().allowedAlgs()),
//                new TypHeaderValidator(validatorPolicy.headerPolicy().allowedTypHeaders()),
//                new AudienceValidator(validatorPolicy.claimPolicy().requiredAudiences()),
//                new AuthorizedPartyValidator(validatorPolicy.claimPolicy().allowedAzp()),
//                new ScopeValidator(validatorPolicy.claimPolicy().requiredScopes()),
//                new SubjectFormatValidator(validatorPolicy.claimPolicy().subjectPattern()),
//                new ExpNbfSkewValidator(validatorPolicy.claimPolicy().clockSkew()),
//                new KidValidator(validatorPolicy.headerPolicy().enforceKidPinning(), validatorPolicy.headerPolicy().allowedKids()),
//                new TenantValidator()
//        ));
//
//        // Only add JTI replay validator if a store is available
//        jtiStoreProvider.ifAvailable(jtiStore -> validators.add(new JtiReplayValidator(jtiStore)));
//
//        return new CompositeJwtValidator(validators);
//    }

}
