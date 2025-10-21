package com.mariosmant.webapp.mediahub.common.json.infrastructure;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@AutoConfiguration
public class JsonConfig {

    @Bean
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        return builder
                .featuresToEnable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, // UnrecognizedPropertyException
                        DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, // InvalidFormatException
                        DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS) // MismatchedInputException if number for enum, InvalidFormatException if invalid string for enum.
                .build()
                .deactivateDefaultTyping(); // disable polymorphic default typing  // InvalidTypeIdException
    }

}
