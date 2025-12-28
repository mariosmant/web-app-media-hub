package com.mariosmant.webapp.mediahub.common.utils;


import java.math.BigDecimal;
import java.math.RoundingMode;

public class BigDecimalUtils {
    public static BigDecimal scale(BigDecimal value, int fraction, RoundingMode roundingMode) {
        if (value == null) return null;
        return value.setScale(fraction, roundingMode);
    }
}
