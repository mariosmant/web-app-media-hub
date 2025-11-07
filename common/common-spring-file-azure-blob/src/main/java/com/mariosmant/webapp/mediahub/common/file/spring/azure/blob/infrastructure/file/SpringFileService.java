package com.mariosmant.webapp.mediahub.common.file.spring.infrastructure.file;

import com.mariosmant.webapp.mediahub.common.file.core.domain.port.FilePort;
import com.mariosmant.webapp.mediahub.common.file.spring.domain.port.SpringFilePort;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;

public class SpringFileService implements SpringFilePort {

    private final AzureFilePort azureFilePort;

    public SpringFileService(AzureFilePort azureFilePort) {
        this.filePort = filePort;
    }

    /**
     * Convenience method: accepts a MultipartFile and delegates
     * to the InputStream-based method.
     */
    @Override
    public void multipartFileToBlob(MultipartFile multipartFile, BlockBlobClient blockBlobClient) throws IOException {
        try (InputStream in = multipartFile.getInputStream()) {
            ParallelTransferOptions options = new ParallelTransferOptions()
                    .setBlockSizeLong(8L * 1024 * 1024) // 8 MB blocks
                    .setMaxConcurrency(4);              // 4 parallel uploads

            blockBlobClient.upload(inputStream, -1, true, options, null, null, null);

        }
    }
}
