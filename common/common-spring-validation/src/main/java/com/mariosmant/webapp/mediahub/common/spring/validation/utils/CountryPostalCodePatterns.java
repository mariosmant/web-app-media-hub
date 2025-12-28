package com.mariosmant.webapp.mediahub.common.spring.validation.utils;

import com.mariosmant.webapp.mediahub.common.spring.validation.enums.CountryPostalCodePattern;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public final class CountryPostalCodePatterns {

    public static final Map<String, Pattern> PATTERNS;

    static {
        Map<String, Pattern> map = new HashMap<>();
        for (CountryPostalCodePattern p : CountryPostalCodePattern.values()) {
            map.put(p.getCountryCode(), Pattern.compile(p.getRegex()));
        }
        PATTERNS = Collections.unmodifiableMap(map);
    }

    private CountryPostalCodePatterns() {}
}

