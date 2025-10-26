package com.mariosmant.webapp.mediahub.common.file.azure.blob.infrastructure.file;

import com.azure.core.credential.AccessToken;
import com.azure.core.credential.TokenCredential;
import com.azure.core.http.HttpAuthorization;
import com.azure.core.util.Context;
import com.azure.core.util.polling.PollResponse;
import com.azure.core.util.polling.SyncPoller;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobCopyInfo;
import com.azure.storage.blob.models.CopyStatusType;
import com.azure.storage.blob.models.ParallelTransferOptions;
import com.azure.storage.blob.models.UserDelegationKey;
import com.azure.storage.blob.options.BlobBeginCopyOptions;
import com.azure.storage.blob.options.BlockBlobOutputStreamOptions;
import com.azure.storage.blob.sas.BlobSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;
import com.azure.storage.blob.specialized.BlockBlobClient;
import com.azure.storage.common.sas.SasProtocol;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.azure.storage.blob.options.BlobCopyFromUrlOptions;

import com.mariosmant.webapp.mediahub.common.file.azure.blob.domain.port.AzureBlobFilePort;

import java.io.*;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class AzureBlobFileService implements AzureBlobFilePort {
    // Tune for your workload
    private static final int IO_BUFFER = 16384;            // 16 KB copy buffer
    private static final int STREAM_BUFFER = 16384;        // 16 KB Buffered streams
    private static final int ZIP_LEVEL = Deflater.BEST_SPEED; // Faster, less CPU
    private static final int COPY_BLOB_TO_BLOB_IO_BUFFER = 32768;   // 32 KB copy buffer
    private static final int COPY_BLOB_TO_BLOB_STREAM_BUFFER = 32768;

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void zipBlobsToBlob(BlobContainerClient sourceContainer, BlobContainerClient targetContainer,
                               List<String> sourceBlobNames,
                               String targetZipName) throws IOException {

        BlockBlobClient target = targetContainer.getBlobClient(targetZipName).getBlockBlobClient();

        // Configure options (block size and concurrency)
        // Open upload stream with conservative options to avoid staging too many blocks in memory
        // Block size defaults to ~4 MB; keep parallelism low so write() backpressures naturally.
        BlockBlobOutputStreamOptions options = new BlockBlobOutputStreamOptions()
                .setParallelTransferOptions(new ParallelTransferOptions()
                        .setBlockSizeLong(4L * 1024L * 1024L) // use SDK default 4MB
                        .setMaxConcurrency(1)); // avoid multiple in-flight blocks per stream

        try (OutputStream blobOut = target.getBlobOutputStream(options);
             BufferedOutputStream bufferedOut = new BufferedOutputStream(blobOut, STREAM_BUFFER);
             ZipOutputStream zipOut = new ZipOutputStream(bufferedOut)) {

            zipOut.setLevel(ZIP_LEVEL);

            for (String name : sourceBlobNames) {
                BlobClient src = sourceContainer.getBlobClient(name);

                // Stream download; small buffer so the loop backpressures on slow upload
                try (InputStream in = new BufferedInputStream(src.openInputStream(), STREAM_BUFFER)) {

                    ZipEntry entry = new ZipEntry(name);
                    // Optional: set size if known (improves Zip metadata; not required for streaming)
                    // entry.setSize(src.getProperties().getBlobSize());
                    zipOut.putNextEntry(entry);

                    byte[] buf = new byte[IO_BUFFER];
                    int n;
                    while ((n = in.read(buf)) != -1) {
                        zipOut.write(buf, 0, n);  // write may block, which is good (backpressure)
                    }

                    zipOut.closeEntry();
                    zipOut.flush();               // push compressed data downstream
                }
            }
            // zipOut close flushes and finishes the zip
        }
    }

    @Override
    public void writeAsJson(Object dto, BlobContainerClient targetContainer, String blobName) throws IOException {
        BlockBlobClient destinationBlob = targetContainer.getBlobClient(blobName).getBlockBlobClient();

        // Configure options (block size and concurrency)
        // Open upload stream with conservative options to avoid staging too many blocks in memory
        // Block size defaults to ~4 MB; keep parallelism low so write() backpressures naturally.
        BlockBlobOutputStreamOptions options = new BlockBlobOutputStreamOptions()
                .setParallelTransferOptions(new ParallelTransferOptions()
                        .setBlockSizeLong(4L * 1024L * 1024L) // use SDK default 4MB
                        .setMaxConcurrency(1)); // avoid multiple in-flight blocks per stream

        try (OutputStream out = new BufferedOutputStream(
                destinationBlob.getBlobOutputStream(options),
                STREAM_BUFFER)) {
            // Stream JSON directly into blob
            mapper.writeValue(out, dto);
            out.flush();
        }
    }

    @Override
    public void copyBlobToBlob(BlobContainerClient srcContainer,
                                String srcName,
                                BlobContainerClient dstContainer,
                                String dstName) throws IOException {

        BlobClient source = srcContainer.getBlobClient(srcName);
        BlockBlobClient dest = dstContainer.getBlobClient(dstName).getBlockBlobClient();

        // Configure options (block size and concurrency)
        // Open upload stream with conservative options to avoid staging too many blocks in memory
        // Block size defaults to ~4 MB; keep parallelism low so write() backpressures naturally.
        BlockBlobOutputStreamOptions options = new BlockBlobOutputStreamOptions()
                .setParallelTransferOptions(new ParallelTransferOptions()
                        .setBlockSizeLong(4L * 1024L * 1024L) // use SDK default 4MB
                        .setMaxConcurrency(1)); // avoid multiple in-flight blocks per stream

        try (InputStream in = new BufferedInputStream(source.openInputStream(), COPY_BLOB_TO_BLOB_STREAM_BUFFER);
             OutputStream out = new BufferedOutputStream(
                     dest.getBlobOutputStream(options),
                     COPY_BLOB_TO_BLOB_STREAM_BUFFER)) {

            byte[] buf = new byte[COPY_BLOB_TO_BLOB_IO_BUFFER];
            int n;
            while ((n = in.read(buf)) != -1) {
                out.write(buf, 0, n);   // blocks if upload is slow → natural backpressure
            }
            out.flush();
        }
    }

    @Override
    public void copyAzureSideBlobToBlob(
            String sourceAccountUrl,
            String sourceContainer,
            String sourceBlobName,
            String targetAccountUrl,
            String targetContainer,
            String targetBlobName) {

        // Managed identity credential
        TokenCredential credential = new DefaultAzureCredentialBuilder().build();
        // TODO Managed identity.

        // Source client
        BlobServiceClient sourceService = new BlobServiceClientBuilder()
                .endpoint(sourceAccountUrl)
                .credential(credential)
                .buildClient();

        BlobClient sourceBlob = sourceService
                .getBlobContainerClient(sourceContainer)
                .getBlobClient(sourceBlobName);

        // Get a user delegation key (valid for a short window)
        OffsetDateTime start = OffsetDateTime.now();
        OffsetDateTime expiry = start.plusHours(1); // adjust as needed
        UserDelegationKey delegationKey =
                sourceService.getUserDelegationKey(start, expiry);

        // Build SAS permissions
        BlobSasPermission permissions = new BlobSasPermission()
                .setReadPermission(true);

        // Build SAS values
        BlobServiceSasSignatureValues sasValues = new BlobServiceSasSignatureValues(expiry, permissions)
                .setStartTime(start)
                .setProtocol(SasProtocol.HTTPS_ONLY);

        // TODO refactor for reuse of above below.

        // Generate SAS token
        String sasToken = sourceBlob.generateUserDelegationSas(sasValues, delegationKey);

        // Build full SAS URL for source blob
        String sourceBlobSasUrl = sourceBlob.getBlobUrl() + "?" + sasToken;

        // Target client (also via Managed Identity)
        BlobServiceClient targetService = new BlobServiceClientBuilder()
                .endpoint(targetAccountUrl)
                .credential(credential)
                .buildClient();

        BlobClient targetBlob = targetService
                .getBlobContainerClient(targetContainer)
                .getBlobClient(targetBlobName);

        // Begin server-side copy using SAS URL
        BlobBeginCopyOptions options = new BlobBeginCopyOptions(sourceBlobSasUrl).setPollInterval(Duration.ofSeconds(2));
        SyncPoller<BlobCopyInfo, Void> poller = targetBlob.beginCopy(options);

        PollResponse<BlobCopyInfo> pollResponse = poller.waitForCompletion();

        BlobCopyInfo info = pollResponse.getValue();
        System.out.println("Copy status: " + info.getCopyStatus());
        if (info.getCopyStatus() == CopyStatusType.SUCCESS) {
            System.out.println("Copy completed successfully.");
        } else if (info.getCopyStatus() == CopyStatusType.FAILED) {
            System.out.println("Copy failed.");
        } else if (info.getCopyStatus() == CopyStatusType.ABORTED) {
            System.out.println("Copy was aborted.");
        }
        // TODO replace system out with return value and respective logs.
    }


    @Override
    public void copyAzureSideBlobToBlob(String sourceBlobUrl, String targetAccountUrl,
                                      String targetContainer, String targetBlobName) {

        // Managed identity credential
        TokenCredential credential = new DefaultAzureCredentialBuilder().build();
        // TODO Managed identity only for prod-like.

        // Acquire OAuth token scoped for Blob service (source authorization header)
        // Scope for Azure Storage: https://storage.azure.com/.default
        AccessToken token = credential.getToken(
                        new com.azure.core.credential.TokenRequestContext()
                                .addScopes("https://storage.azure.com/.default"))
                .block();
        if(token == null) {
            // TODO throw error.
        }
        HttpAuthorization sourceAuth = new HttpAuthorization("Bearer", token.getToken());

        // Target client (authorized via managed identity)
        BlobServiceClient targetService = new BlobServiceClientBuilder()
                .endpoint(targetAccountUrl)
                .credential(credential)
                .buildClient();

        BlobClient targetBlob = targetService.getBlobContainerClient(targetContainer)
                .getBlobClient(targetBlobName);

        // Copy options with source OAuth
        BlobBeginCopyOptions options = new BlobBeginCopyOptions(sourceBlobUrl)
                .setA(sourceAuth);

        // Server-side copy (RAM-safe, function network not used)
        SyncPoller<BlobCopyInfo, Void> poller = targetBlob.beginCopy(options, Duration.ofSeconds(2), Context.NONE);
        poller.waitForCompletion();

        BlobCopyInfo info = poller.getFinalResult();
        if (info.getCopyStatus() == CopyStatusType.SUCCESS) {
            System.out.println("Copy completed successfully.");
        } else if (info.getCopyStatus() == CopyStatusType.FAILED) {
            System.out.println("Copy failed.");
        } else if (info.getCopyStatus() == CopyStatusType.ABORTED) {
            System.out.println("Copy was aborted.");
        }
    }
}
