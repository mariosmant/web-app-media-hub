package com.mariosmant.webapp.mediahub.common.spring.validation.providers;

import java.util.Map;
import java.util.regex.Pattern;

public interface CountryPostalCodePatternProvider {
    Map<String, Pattern> getCountryPostalCodePatterns();
}
