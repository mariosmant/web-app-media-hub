package com.mariosmant.webapp.mediahub.forms.service.validation.providers;

import com.mariosmant.webapp.mediahub.common.spring.validation.providers.PostalCodePatternProvider;
import com.mariosmant.webapp.mediahub.forms.service.domain.service.CountryService;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.regex.Pattern;

@Component
public class FormPostalCodePatternProvider implements PostalCodePatternProvider {

    private final CountryService countryService;

    public FormPostalCodePatternProvider(CountryService countryService) {
        this.countryService = countryService;
    }

    @Override
    public Map<String, Pattern> getPostalCodePatterns() {
        return countryService.getCountriesAndPostalCodePatterns();
        // TODO return repo.fetchPatternsFromDb(); // or Redis
    }
}
