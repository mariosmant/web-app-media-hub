package com.mariosmant.webapp.mediahub.common.spring.validation.validators;

import com.mariosmant.webapp.mediahub.common.spring.validation.annotations.ValidCountryPostalCode;
import com.mariosmant.webapp.mediahub.common.spring.validation.contract.PostalCodeValidationData;
import com.mariosmant.webapp.mediahub.common.spring.validation.providers.CountryPostalCodePatternProvider;
import com.mariosmant.webapp.mediahub.common.spring.validation.utils.CountryPostalCodePatterns;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Map;
import java.util.regex.Pattern;

public class ValidCountryPostalCodeValidator implements ConstraintValidator<ValidCountryPostalCode, PostalCodeValidationData> {


    private Map<String, Pattern> patterns;
    private boolean validateCountry;

    @Override
    public void initialize(ValidCountryPostalCode annotation) {
        Class<? extends CountryPostalCodePatternProvider> providerClass = annotation.provider();
        this.validateCountry = annotation.validateCountry();

        // If no provider was supplied → use default static patterns
        if (providerClass == CountryPostalCodePatternProvider.class) {
            this.patterns = CountryPostalCodePatterns.PATTERNS;
            return;
        }

        try {
            CountryPostalCodePatternProvider provider = providerClass.getDeclaredConstructor().newInstance();
            this.patterns = provider.getCountryPostalCodePatterns();
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

        if (validateCountry && !patterns.containsKey(country)) {
            return false;
        }

        Pattern pattern = patterns.get(country);
        if (pattern == null) return true;

        return pattern.matcher(postalCode).matches();
    }

}

