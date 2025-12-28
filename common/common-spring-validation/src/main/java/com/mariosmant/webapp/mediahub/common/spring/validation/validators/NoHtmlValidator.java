package com.mariosmant.webapp.mediahub.common.spring.validation.validators;

import com.mariosmant.webapp.mediahub.common.spring.validation.annotations.NoHtml;

public class NoHtmlValidator implements jakarta.validation.ConstraintValidator<NoHtml, String> {
    @Override
    public boolean isValid(String value, jakarta.validation.ConstraintValidatorContext context) {
        if (value == null) return true;
        String v = value.trim();
        return !(v.contains("<") || v.contains(">"));
    }
}