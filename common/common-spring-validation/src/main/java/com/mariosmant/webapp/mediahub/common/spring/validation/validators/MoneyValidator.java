package com.mariosmant.webapp.mediahub.common.spring.validation.validators;

import com.mariosmant.webapp.mediahub.common.spring.validation.annotations.Money;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.math.BigDecimal;

public class MoneyValidator implements ConstraintValidator<Money, BigDecimal> {

    private int integer;
    private int fraction;
    private BigDecimal min;
    private BigDecimal max;

    @Override
    public void initialize(Money annotation) {
        this.integer = annotation.integer();
        this.fraction = annotation.fraction();
        this.min = new BigDecimal(annotation.min());
        this.max = new BigDecimal(annotation.max());
    }

    @Override
    public boolean isValid(BigDecimal value, ConstraintValidatorContext ctx) {
        if (value == null) {
            return true; // @NotNull should handle null
        }

        // Check range
        if (value.compareTo(min) < 0 || value.compareTo(max) > 0) {
            return false;
        }

        // Check precision
        int intDigits = value.precision() - value.scale();
        int fracDigits = Math.max(value.scale(), 0);

        return intDigits <= integer && fracDigits <= fraction;
    }
}
