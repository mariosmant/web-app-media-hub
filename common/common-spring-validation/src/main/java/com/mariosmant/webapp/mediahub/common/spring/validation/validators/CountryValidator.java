package com.mariosmant.webapp.mediahub.common.spring.validation.validators;

import com.mariosmant.webapp.mediahub.common.spring.validation.annotations.CountryCode;
import com.mariosmant.webapp.mediahub.common.spring.validation.providers.CountryCodeProvider;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Locale;
import java.util.Set;

public class CountryValidator implements ConstraintValidator<CountryCode, String> {

    private static final Set<String> ISO_COUNTRIES =
            Set.of(Locale.getISOCountries());

    private Set<String> countryCodes;

    @Override
    public void initialize(CountryCode annotation) {
        Class<? extends CountryCodeProvider> providerClass = annotation.provider();

        // If no provider was supplied → use default ISO list
        if (providerClass == CountryCodeProvider.class) {
            this.countryCodes = ISO_COUNTRIES;
            return;
        }

        try {
            CountryCodeProvider provider = providerClass.getDeclaredConstructor().newInstance();
            this.countryCodes = provider.getCountryCodes();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to instantiate CountryCodeProvider", e);
        }

        if(this.countryCodes == null) {
            throw new IllegalStateException("countryCodes cannot be null");
        }
    }


    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) return true;
        return countryCodes.contains(value);
    }

}
