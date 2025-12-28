package com.mariosmant.webapp.mediahub.common.spring.validation.annotations;

import com.mariosmant.webapp.mediahub.common.spring.validation.validators.MoneyValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MoneyValidator.class)
@Documented
public @interface Money {

    String message() default "Invalid monetary amount";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int integer() default 10;

    int fraction() default 2;

    String min() default "0.00";

    String max() default "9999999999.99";
}
