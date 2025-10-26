package com.mariosmant.webapp.mediahub.common.file.azure.blob.infrastructure.spring.config;

import com.mariosmant.webapp.mediahub.common.file.azure.blob.domain.port.AzureBlobFilePort;
import com.mariosmant.webapp.mediahub.common.file.azure.blob.infrastructure.file.AzureBlobFileService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class AzureBlobFileAutoConfiguration {

    @Bean
    public AzureBlobFilePort azureBlobfilePort() {
        return new AzureBlobFileService();
    }
}
