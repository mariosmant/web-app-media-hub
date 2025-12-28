package com.mariosmant.webapp.mediahub.common.spring.validation.annotations;

import com.mariosmant.webapp.mediahub.common.spring.validation.validators.NoHtmlValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NoHtmlValidator.class)
public @interface NoHtml {
    String message() default "HTML content is not allowed";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

