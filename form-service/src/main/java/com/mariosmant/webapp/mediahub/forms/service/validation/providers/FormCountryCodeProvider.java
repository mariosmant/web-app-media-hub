package com.mariosmant.webapp.mediahub.forms.service.validation.providers;

import com.mariosmant.webapp.mediahub.common.spring.validation.providers.CountryCodeProvider;
import com.mariosmant.webapp.mediahub.forms.service.domain.service.CountryService;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class FormCountryCodeProvider implements CountryCodeProvider {

    private final CountryService countryService;

    public FormCountryCodeProvider(CountryService countryService) {
        this.countryService = countryService;
    }

    @Override
    public Set<String> getCountryCodes() {
        return countryService.getCountriesAndPostalCodePatterns().keySet();
    }


}
