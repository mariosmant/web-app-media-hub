package com.mariosmant.webapp.mediahub.common.spring.file.azure.blob.infrastructure.file;

import com.azure.storage.blob.options.BlockBlobOutputStreamOptions;
import com.azure.storage.blob.specialized.BlockBlobClient;
import com.mariosmant.webapp.mediahub.common.file.azure.blob.domain.port.FileAzureBlobPort;
import com.mariosmant.webapp.mediahub.common.spring.file.azure.blob.domain.port.SpringFileAzureBlobPort;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import com.azure.storage.blob.models.ParallelTransferOptions;

public class SpringFileAzureBlobService implements SpringFileAzureBlobPort {

    private final FileAzureBlobPort fileAzureBlobPort;

    public SpringFileAzureBlobService(FileAzureBlobPort fileAzureBlobPort) {
        this.fileAzureBlobPort = fileAzureBlobPort;
    }

    /**
     * Convenience method: accepts a MultipartFile and delegates
     * to the InputStream-based method.
     */
    @Override
    public void multipartFileToBlob(MultipartFile multipartFile, BlockBlobClient blockBlobClient) throws IOException {
        BlockBlobOutputStreamOptions options = new BlockBlobOutputStreamOptions()
                .setParallelTransferOptions(new ParallelTransferOptions()
                        .setBlockSizeLong(1024L * 1024L)
                        .setMaxConcurrency(1)); // avoid multiple in-flight blocks per stream


        try (InputStream in = multipartFile.getInputStream()) {
            // TODO.
        }
    }
}
