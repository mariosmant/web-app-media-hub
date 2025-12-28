package com.mariosmant.webapp.mediahub.common.spring.validation.annotations;

import com.mariosmant.webapp.mediahub.common.spring.validation.providers.CountryCodeProvider;
import com.mariosmant.webapp.mediahub.common.spring.validation.providers.PostalCodePatternProvider;
import com.mariosmant.webapp.mediahub.common.spring.validation.validators.ValidCountryPostalCodeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidCountryPostalCodeValidator.class)
public @interface ValidCountryPostalCode {
    Class<? extends PostalCodePatternProvider> provider() default PostalCodePatternProvider.class;
    String message() default "Postal code does not match country format";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
