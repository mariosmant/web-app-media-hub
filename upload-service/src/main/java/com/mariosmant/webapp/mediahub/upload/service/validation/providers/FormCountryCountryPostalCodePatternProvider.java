package com.mariosmant.webapp.mediahub.upload.service.validation.providers;

import com.mariosmant.webapp.mediahub.common.spring.validation.providers.CountryPostalCodePatternProvider;
import com.mariosmant.webapp.mediahub.upload.service.domain.service.CountryService;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.regex.Pattern;

@Component
public class FormCountryCountryPostalCodePatternProvider implements CountryPostalCodePatternProvider {

    private final CountryService countryService;

    public FormCountryCountryPostalCodePatternProvider(CountryService countryService) {
        this.countryService = countryService;
    }

    @Override
    public Map<String, Pattern> getCountryPostalCodePatterns() {
        return countryService.getCountriesAndPostalCodePatterns();
    }
}
