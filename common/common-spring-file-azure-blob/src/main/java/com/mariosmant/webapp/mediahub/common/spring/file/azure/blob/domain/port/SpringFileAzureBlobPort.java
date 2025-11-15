package com.mariosmant.webapp.mediahub.common.spring.file.azure.blob.domain.port;

import org.springframework.web.multipart.MultipartFile;
import com.azure.storage.blob.specialized.BlockBlobClient;

import java.io.IOException;

public interface SpringFileAzureBlobPort {
    void multipartFileToBlob(MultipartFile multipartFile, BlockBlobClient blockBlobClient) throws IOException;
}
