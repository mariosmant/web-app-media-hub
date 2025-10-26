package com.mariosmant.webapp.mediahub.common.file.azure.blob.domain.port;

import com.azure.storage.blob.BlobContainerClient;

import java.io.IOException;
import java.util.List;

public interface AzureBlobFilePort {
    void zipBlobsToBlob(BlobContainerClient sourceContainer, BlobContainerClient targetContainer,
                        List<String> sourceBlobNames,
                        String targetZipName) throws IOException;

    void writeAsJson(Object dto, BlobContainerClient targetContainer, String blobName) throws IOException;

    void copyBlobToBlob(BlobContainerClient srcContainer,
                        String srcName,
                        BlobContainerClient dstContainer,
                        String dstName) throws IOException;

    void copyAzureSideBlobToBlob(String sourceBlobUrl, String targetAccountUrl,
                                 String targetContainer, String targetBlobName);
}
