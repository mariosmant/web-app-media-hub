package com.mariosmant.webapp.mediahub.common.file.core.infrastructure.spring.config;


import com.mariosmant.webapp.mediahub.common.file.core.domain.port.FilePort;
import com.mariosmant.webapp.mediahub.common.file.core.infrastructure.file.FileService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class FileAutoConfiguration {

    @Bean
    public FilePort filePort() {
        return new FileService();
    }
}
