package com.mariosmant.webapp.mediahub.forms.service.domain.service;

import com.mariosmant.webapp.mediahub.common.spring.validation.utils.CountryPostalCodePatterns;
import jakarta.validation.constraints.NotNull;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.regex.Pattern;

@Service
public class CountryService {

    @Cacheable("countriesAndPostalCodePatterns")
    public @NotNull Map<String, Pattern> getCountriesAndPostalCodePatterns() {
        return CountryPostalCodePatterns.PATTERNS;
        // TODO return repo.fetchPatternsFromDb(); // or Redis
    }
}
