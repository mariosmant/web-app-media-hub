package com.mariosmant.webapp.mediahub.common.security.core.infrastructure.spring.config;

import com.mariosmant.webapp.mediahub.common.security.context.domain.service.JtiStore;
import com.mariosmant.webapp.mediahub.common.security.core.infrastructure.spring.properties.SecurityProperties;
import com.mariosmant.webapp.mediahub.common.security.core.infrastructure.spring.properties.ValidatorPolicy;
import com.mariosmant.webapp.mediahub.common.security.core.infrastructure.spring.validator.*;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.JwsAlgorithm;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Configuration
public class JwtDecoderConfig {

    @Bean
    public NimbusJwtDecoder jwtDecoder(SecurityProperties props,
                                       OAuth2TokenValidator<Jwt> compositeValidator) {
        NimbusJwtDecoder decoder = NimbusJwtDecoder
                .withJwkSetUri(props.jwkSetUri())
                // Algorithm pinning: RSA-PSS (PS256) only.
                .jwsAlgorithms(signatureAlgorithms ->
                        props.validatorPolicy().algorithmPolicy().allowedAlgs().forEach(alg -> signatureAlgorithms.add(SignatureAlgorithm.valueOf(alg)))).build();
        decoder.setJwtValidator(compositeValidator);
        return decoder;
    }

    @Bean
    public OAuth2TokenValidator<Jwt> compositeValidator(SecurityProperties props,
                                                        ObjectProvider<JtiStore> jtiStoreProvider) {
        ValidatorPolicy validatorPolicy = props.validatorPolicy();

        List<OAuth2TokenValidator<Jwt>> validators = new ArrayList<>();

        addValidatorIf(validatorPolicy.isIssuer(),
                () -> JwtValidators.createDefaultWithIssuer(props.issuer()), validators);

        addValidatorIf(validatorPolicy.isAlgorithm(),
                () -> new AlgorithmValidator(validatorPolicy.algorithmPolicy().allowedAlgs()), validators);

        addValidatorIf(validatorPolicy.isTypHeader(),
                () -> new TypHeaderValidator(validatorPolicy.headerPolicy().allowedTypHeaders()), validators);

        addValidatorIf(validatorPolicy.isAudience(),
                () -> new AudienceValidator(validatorPolicy.claimPolicy().requiredAudiences()), validators);

        addValidatorIf(validatorPolicy.isAuthorizedParty(),
                () -> new AuthorizedPartyValidator(validatorPolicy.claimPolicy().allowedAzp()), validators);

        addValidatorIf(validatorPolicy.isScope(),
                () -> new ScopeValidator(validatorPolicy.claimPolicy().requiredScopes()), validators);

        addValidatorIf(validatorPolicy.isSubjectFormat(),
                () -> new SubjectFormatValidator(validatorPolicy.claimPolicy().subjectPattern()), validators);

        addValidatorIf(validatorPolicy.isExpNbfSkew(),
                () -> new ExpNbfSkewValidator(validatorPolicy.claimPolicy().clockSkew()), validators);

        addValidatorIf(validatorPolicy.isKid(),
                () -> new KidValidator(validatorPolicy.headerPolicy().enforceKidPinning(),
                        validatorPolicy.headerPolicy().allowedKids()), validators);

        addValidatorIf(validatorPolicy.isTenant(),
                TenantValidator::new, validators);

        if (validatorPolicy.isJti()) {
            jtiStoreProvider.ifAvailable(store -> validators.add(new JtiReplayValidator(store)));
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
