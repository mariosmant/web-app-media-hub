package com.mariosmant.webapp.mediahub.common.spring.validation.annotations;

import com.mariosmant.webapp.mediahub.common.spring.validation.providers.CountryCodeProvider;
import com.mariosmant.webapp.mediahub.common.spring.validation.validators.CountryValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CountryValidator.class)
@Documented
public @interface CountryCode {

    Class<? extends CountryCodeProvider> provider() default CountryCodeProvider.class;

    String message() default "Invalid country code";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
