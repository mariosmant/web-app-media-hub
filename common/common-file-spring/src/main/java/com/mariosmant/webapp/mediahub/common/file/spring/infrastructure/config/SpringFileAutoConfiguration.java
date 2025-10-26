package com.mariosmant.webapp.mediahub.common.file.spring.infrastructure.config;


import com.mariosmant.webapp.mediahub.common.file.core.domain.port.FilePort;
import com.mariosmant.webapp.mediahub.common.file.spring.domain.port.SpringFilePort;
import com.mariosmant.webapp.mediahub.common.file.spring.infrastructure.file.SpringFileService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class SpringFileAutoConfiguration {

    @Bean
    public SpringFilePort springFilePort(FilePort filePort) {
        return new SpringFileService(filePort);
    }
}
