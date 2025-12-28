package com.mariosmant.webapp.mediahub.common.spring.validation.providers;

import java.util.Map;
import java.util.regex.Pattern;

public interface PostalCodePatternProvider {
    Map<String, Pattern> getPostalCodePatterns();
}
