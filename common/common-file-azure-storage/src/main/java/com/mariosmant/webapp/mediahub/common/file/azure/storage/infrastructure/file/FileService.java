package com.mariosmant.webapp.mediahub.common.file.core.infrastructure.file;

import com.mariosmant.webapp.mediahub.common.file.core.domain.port.FilePort;

import java.io.*;
import java.nio.file.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.azure.storage.blob.*;
import java.io.*;
import java.nio.file.*;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.storage.blob.models.*;
import com.azure.storage.blob.specialized.BlockBlobClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.nio.file.*;
import java.util.List;
import java.util.zip.*;

// TODO refactor.
public class FileService implements FilePort {

    private static final int DEFAULT_COPY_FILE_TO_FILE_IO_BUFFER = 32 * 1024;
    private static final int DEFAULT_ZIP_FILES_TO_FILE_IO_BUFFER = 32 * 1024;




    public class WritePojoToBlob {

        private static final int STREAM_BUFFER = 16384; // 16 KB

        private final BlobContainerClient container;
        private final ObjectMapper mapper = new ObjectMapper();

        public WritePojoToBlob(String accountUrl, String containerName) {
            BlobServiceClient service = new BlobServiceClientBuilder()
                    .endpoint(accountUrl)
                    .credential(new DefaultAzureCredentialBuilder().build())
                    .buildClient();
            this.container = service.getBlobContainerClient(containerName);
        }

        public void writeAsJson(Object dto, String blobName) throws IOException {
            BlockBlobClient dest = container.getBlobClient(blobName).getBlockBlobClient();

            ParallelTransferOptions pto = new ParallelTransferOptions()
                    .setMaxConcurrency(1); // ensures backpressure

            try (OutputStream out = new BufferedOutputStream(
                    dest.getBlobOutputStream(pto, null, null, null),
                    STREAM_BUFFER)) {
                // Stream JSON directly into blob
                mapper.writeValue(out, dto);
                out.flush();
            }
        }

    }



    public class CopyBlobToFileShare {

        private static final int IO_BUFFER = 32768;   // 32 KB copy buffer
        private static final int STREAM_BUFFER = 32768;

        public static void copyBlobToFile(BlobContainerClient container,
                                          String blobName,
                                          Path fileSharePath) throws IOException {

            BlobClient source = container.getBlobClient(blobName);

            // Open blob input stream
            try (InputStream in = new BufferedInputStream(source.openInputStream(), STREAM_BUFFER);
                 OutputStream out = new BufferedOutputStream(
                         Files.newOutputStream(fileSharePath,
                                 StandardOpenOption.CREATE,
                                 StandardOpenOption.TRUNCATE_EXISTING),
                         STREAM_BUFFER)) {

                byte[] buf = new byte[IO_BUFFER];
                int n;
                while ((n = in.read(buf)) != -1) {
                    out.write(buf, 0, n);   // SMB write blocks if network is slow → natural backpressure
                }
                out.flush();
            }
        }
    }


    public class ZipBlobsToFileShare {

        private static final int IO_BUFFER = 16384;             // 16 KB copy buffer
        private static final int STREAM_BUFFER = 16384;         // 16 KB Buffered streams
        private static final int ZIP_LEVEL = Deflater.BEST_SPEED;

        public static void zipBlobsToFileShare(BlobContainerClient container,
                                               List<String> sourceBlobNames,
                                               Path shareZipPath) throws IOException {

            // Ensure the share is mounted locally with low-latency network (same region/VNET if possible)
            try (OutputStream fileOut = Files.newOutputStream(shareZipPath,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
                 BufferedOutputStream bufferedOut = new BufferedOutputStream(fileOut, STREAM_BUFFER);
                 ZipOutputStream zipOut = new ZipOutputStream(bufferedOut)) {

                zipOut.setLevel(ZIP_LEVEL);

                for (String name : sourceBlobNames) {
                    BlobClient src = container.getBlobClient(name);

                    try (InputStream in = new BufferedInputStream(src.openInputStream(), STREAM_BUFFER)) {

                        zipOut.putNextEntry(new ZipEntry(name));

                        byte[] buf = new byte[IO_BUFFER];
                        int n;
                        while ((n = in.read(buf)) != -1) {
                            zipOut.write(buf, 0, n);  // SMB write may block; that’s desired backpressure
                        }

                        zipOut.closeEntry();
                        zipOut.flush();
                    }
                }
            }
        }
    }



    public class CopyBlobToFileShare {

        private static final int IO_BUFFER = 32768;   // 32 KB copy buffer
        private static final int STREAM_BUFFER = 32768;

        public static void copyBlobToFile(BlobContainerClient container,
                                          String blobName,
                                          Path fileSharePath) throws IOException {

            BlobClient source = container.getBlobClient(blobName);

            // Open blob input stream
            try (InputStream in = new BufferedInputStream(source.openInputStream(), STREAM_BUFFER);
                 OutputStream out = new BufferedOutputStream(
                         Files.newOutputStream(fileSharePath,
                                 StandardOpenOption.CREATE,
                                 StandardOpenOption.TRUNCATE_EXISTING),
                         STREAM_BUFFER)) {

                byte[] buf = new byte[IO_BUFFER];
                int n;
                while ((n = in.read(buf)) != -1) {
                    out.write(buf, 0, n);   // SMB write blocks if network is slow → natural backpressure
                }
                out.flush();
            }
        }
    }


    public class CopyFileShareToBlob {

        private static final int IO_BUFFER = 32768;             // 32 KB copy buffer
        private static final int STREAM_BUFFER = 32768;

        public static void copyFileToBlob(Path shareFile, BlobClient destBlob) throws IOException {

            ParallelTransferOptions pto = new ParallelTransferOptions()
                    .setBlockSizeLong(null)
                    .setMaxConcurrency(1);   // single in-flight block for predictable memory

            try (InputStream in = new BufferedInputStream(Files.newInputStream(shareFile), STREAM_BUFFER);
                 OutputStream blobOut = destBlob.getBlockBlobClient().getBlobOutputStream(pto, null, null, null);
                 BufferedOutputStream bufferedOut = new BufferedOutputStream(blobOut, STREAM_BUFFER)) {

                byte[] buf = new byte[IO_BUFFER];
                int n;
                while ((n = in.read(buf)) != -1) {
                    bufferedOut.write(buf, 0, n);
                }
                bufferedOut.flush();
            }
        }
    }






    @Override
    public void copyFileToFile(Path source, Path destination) throws IOException {
        Files.createDirectories(destination.getParent());

        int ioBufferSize = DEFAULT_COPY_FILE_TO_FILE_IO_BUFFER;

        try (InputStream in = new BufferedInputStream(Files.newInputStream(source), ioBufferSize);
             OutputStream out = new BufferedOutputStream(Files.newOutputStream(destination,
                     StandardOpenOption.CREATE,
                     StandardOpenOption.TRUNCATE_EXISTING,
                     StandardOpenOption.WRITE), ioBufferSize)) {

            byte[] buffer = new byte[ioBufferSize];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            out.flush();
        }
    }

    /**
     * Compresses a list of files into a single zip archive.
     *
     * @param sources     list of files to compress
     * @param destination path to the resulting zip file
     */
    @Override
    public void zipFiles(List<Path> sources, Path destination) throws IOException {
        Files.createDirectories(destination.getParent());

        int ioBufferSize = DEFAULT_ZIP_FILES_TO_FILE_IO_BUFFER;

        try (ZipOutputStream zos = new ZipOutputStream(
                new BufferedOutputStream(Files.newOutputStream(destination,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING,
                        StandardOpenOption.WRITE), ioBufferSize))) {

            byte[] buffer = new byte[ioBufferSize];

            for (Path src : sources) {
                if (!Files.isRegularFile(src)) {
                    continue; // skip directories or non-regular files
                }
                try (InputStream in = new BufferedInputStream(Files.newInputStream(src), ioBufferSize)) {
                    ZipEntry entry = new ZipEntry(src.getFileName().toString());
                    zos.putNextEntry(entry);

                    int bytesRead;
                    while ((bytesRead = in.read(buffer)) != -1) {
                        zos.write(buffer, 0, bytesRead);
                    }
                    zos.closeEntry();
                }
            }
            zos.flush();
        }
    }

    /**
     * Core method: writes any InputStream to a file.
     * Safe for large files (streaming, buffered, no OOM).
     */
    public void writeToFile(InputStream inputStream, Path destination) throws IOException {
        Files.createDirectories(destination.getParent());

        try (InputStream in = new BufferedInputStream(inputStream, STREAM_BUFFER);
             OutputStream out = new BufferedOutputStream(
                     Files.newOutputStream(destination,
                             StandardOpenOption.CREATE,
                             StandardOpenOption.TRUNCATE_EXISTING,
                             StandardOpenOption.WRITE),
                     STREAM_BUFFER)) {

            byte[] buf = new byte[IO_BUFFER];
            int n;
            while ((n = in.read(buf)) != -1) {
                out.write(buf, 0, n);
            }
            out.flush();
        }
    }

    /**
     * Convenience method: accepts a MultipartFile and delegates
     * to the InputStream-based method.
     */
    public void writeToFile(MultipartFile multipartFile, Path destination) throws IOException {
        try (InputStream in = multipartFile.getInputStream()) {
            writeToFile(in, destination);
        }

    }
