package com.mariosmant.webapp.mediahub.common.spring.validation.annotations;

import com.mariosmant.webapp.mediahub.common.spring.validation.providers.CountryPostalCodePatternProvider;
import com.mariosmant.webapp.mediahub.common.spring.validation.validators.ValidCountryPostalCodeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidCountryPostalCodeValidator.class)
public @interface ValidCountryPostalCode {
    Class<? extends CountryPostalCodePatternProvider> provider() default CountryPostalCodePatternProvider.class;
    boolean validateCountry() default false;
    String message() default "Postal code does not match country format";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
