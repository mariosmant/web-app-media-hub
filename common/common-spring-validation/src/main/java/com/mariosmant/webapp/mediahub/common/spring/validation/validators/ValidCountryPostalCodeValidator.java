package com.mariosmant.webapp.mediahub.common.spring.validation.validators;

import com.mariosmant.webapp.mediahub.common.spring.validation.annotations.ValidCountryPostalCode;
import com.mariosmant.webapp.mediahub.common.spring.validation.contract.PostalCodeValidationData;
import com.mariosmant.webapp.mediahub.common.spring.validation.providers.PostalCodePatternProvider;
import com.mariosmant.webapp.mediahub.common.spring.validation.utils.PostalCodePatterns;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Map;
import java.util.regex.Pattern;

public class ValidCountryPostalCodeValidator implements ConstraintValidator<ValidCountryPostalCode, PostalCodeValidationData> {


    private Map<String, Pattern> patterns;

    @Override
    public void initialize(ValidCountryPostalCode annotation) {
        Class<? extends PostalCodePatternProvider> providerClass = annotation.provider();

        // If no provider was supplied → use default static patterns
        if (providerClass == PostalCodePatternProvider.class) {
            this.patterns = PostalCodePatterns.PATTERNS;
            return;
        }

        try {
            PostalCodePatternProvider provider = providerClass.getDeclaredConstructor().newInstance();
            this.patterns = provider.getPostalCodePatterns();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to instantiate PostalCodePatternProvider", e);
        }

        if(this.patterns == null) {
            throw new IllegalStateException("patterns cannot be null");
        }
    }

    @Override
    public boolean isValid(PostalCodeValidationData data, ConstraintValidatorContext context) {
        if (data == null) return true;

        String country = data.postalCodeValidationCountry();
        String postalCode = data.postalCodeValidationPostalCode();

        if (country == null || country.isBlank() || postalCode == null) return true;

        Pattern pattern = patterns.get(country);
        if (pattern == null) return true;

        return pattern.matcher(postalCode).matches();
    }

}

