package com.mariosmant.webapp.mediahub.common.file.azure.blob.infrastructure.file;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.models.ParallelTransferOptions;
import com.azure.storage.blob.options.BlockBlobOutputStreamOptions;
import com.azure.storage.blob.specialized.BlockBlobClient;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.mariosmant.webapp.mediahub.common.file.azure.blob.domain.port.FileAzureBlobPort;
import com.mariosmant.webapp.mediahub.common.stream.infrastructure.file.StreamService;

import java.io.*;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileAzureBlobService implements FileAzureBlobPort {
    private static final int DEFAULT_IO_BUFFER_SIZE = 16384;            // 16 KB copy buffer
    private static final int DEFAULT_STREAM_BUFFER_SIZE = 16384;        // 16 KB Buffered streams
    private static final int DEFAULT_ZIP_LEVEL = Deflater.DEFAULT_COMPRESSION;
//    private static final int DEFAULT_COPY_BLOB_TO_BLOB_IO_BUFFER_SIZE = 32768;   // 32 KB copy buffer
//    private static final int DEFAULT_COPY_BLOB_TO_BLOB_STREAM_BUFFER_SIZE = 32768;

    private final ObjectMapper mapper = new ObjectMapper();

    private final StreamService streamService = new StreamService();

    // TODO Parameterize with Options object.
    @Override
    public void zipBlobsToBlob(BlobContainerClient sourceContainer, BlobContainerClient targetContainer,
                               List<String> sourceBlobNames,
                               String targetZipName) throws IOException {
        int ioBufferSize = DEFAULT_IO_BUFFER_SIZE;
        int streamBufferSize = DEFAULT_STREAM_BUFFER_SIZE;

        BlockBlobClient target = targetContainer.getBlobClient(targetZipName).getBlockBlobClient();

        // Configure options (block size and concurrency)
        // Open upload stream with conservative options to avoid staging too many blocks in memory
        // Block size defaults to ~4 MB; keep parallelism low so write() backpressures naturally.
        BlockBlobOutputStreamOptions options = new BlockBlobOutputStreamOptions()
                .setParallelTransferOptions(new ParallelTransferOptions()
                        .setBlockSizeLong(1024L * 1024L)
                        .setMaxConcurrency(1)); // avoid multiple in-flight blocks per stream

        try (OutputStream blobOut = target.getBlobOutputStream(options);
             BufferedOutputStream bufferedOut = new BufferedOutputStream(blobOut, DEFAULT_STREAM_BUFFER_SIZE);
             ZipOutputStream zipOut = new ZipOutputStream(bufferedOut)) {

            zipOut.setLevel(DEFAULT_ZIP_LEVEL);

            for (String name : sourceBlobNames) {
                BlobClient src = sourceContainer.getBlobClient(name);

                // Stream download; small buffer so the loop backpressures on slow upload
                try (InputStream in = new BufferedInputStream(src.openInputStream(), DEFAULT_STREAM_BUFFER_SIZE)) {

                    ZipEntry entry = new ZipEntry(name);
                    // Optional: set size if known (improves Zip metadata; not required for streaming)
                    entry.setSize(src.getProperties().getBlobSize());
                    zipOut.putNextEntry(entry);

                    byte[] buf = new byte[DEFAULT_IO_BUFFER_SIZE];

                    streamService.readInputWriteOutputStream(in, zipOut, buf, false);

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
                        .setBlockSizeLong(1L * 1024L * 1024L)
                        .setMaxConcurrency(1)); // avoid multiple in-flight blocks per stream

        try (OutputStream out = new BufferedOutputStream(
                destinationBlob.getBlobOutputStream(options),
                DEFAULT_STREAM_BUFFER_SIZE)) {
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
                        .setBlockSizeLong(1L * 1024L * 1024L)
                        .setMaxConcurrency(1)); // avoid multiple in-flight blocks per stream

        try (InputStream in = new BufferedInputStream(source.openInputStream(), DEFAULT_STREAM_BUFFER_SIZE);
             OutputStream out = new BufferedOutputStream(
                     dest.getBlobOutputStream(options),
                     DEFAULT_STREAM_BUFFER_SIZE)) {

            byte[] buf = new byte[DEFAULT_IO_BUFFER_SIZE];
            streamService.readInputWriteOutputStream(in, out, buf, true);
        }
    }

    // TODO Add stream to blob.
}
