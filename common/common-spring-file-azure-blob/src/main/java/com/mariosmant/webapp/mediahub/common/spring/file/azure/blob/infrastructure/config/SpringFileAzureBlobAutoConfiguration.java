package com.mariosmant.webapp.mediahub.common.spring.file.azure.blob.infrastructure.config;

import com.mariosmant.webapp.mediahub.common.file.azure.blob.domain.port.FileAzureBlobPort;
import com.mariosmant.webapp.mediahub.common.file.azure.blob.infrastructure.file.FileAzureBlobService;
import com.mariosmant.webapp.mediahub.common.spring.file.azure.blob.domain.port.SpringFileAzureBlobPort;
import com.mariosmant.webapp.mediahub.common.spring.file.azure.blob.infrastructure.file.SpringFileAzureBlobService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class SpringFileAzureBlobAutoConfiguration {

    @Bean
    public FileAzureBlobPort fileAzureBlobPort() {
        return new FileAzureBlobService();
    }

    @Bean
    public SpringFileAzureBlobPort springFileAzureBlobPort() {
        return new SpringFileAzureBlobService(fileAzureBlobPort());
    }
}
