package com.mariosmant.webapp.mediahub.common.file.azure.fileshare.infrastructure.file;

import com.mariosmant.webapp.mediahub.common.file.azure.fileshare.domain.port.AzureFileShareFilePort;

import java.io.*;
import java.nio.file.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

// TODO refactor.
public class AzureFileShareFileService implements AzureFileShareFilePort {

    private static final int DEFAULT_COPY_FILE_TO_FILE_IO_BUFFER = 32 * 1024;
    private static final int DEFAULT_ZIP_FILES_TO_FILE_IO_BUFFER = 32 * 1024;
    private static final int DEFAULT_WRITE_TO_FILE_STREAM_BUFFER = 32 * 1024;
    private static final int DEFAULT_WRITE_TO_FILE_IO_BUFFER = 32 * 1024;

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

        int streamBufferSize = DEFAULT_WRITE_TO_FILE_STREAM_BUFFER;

        int ioBufferSize = DEFAULT_WRITE_TO_FILE_IO_BUFFER;

        try (InputStream in = new BufferedInputStream(inputStream, streamBufferSize);
             OutputStream out = new BufferedOutputStream(
                     Files.newOutputStream(destination,
                             StandardOpenOption.CREATE,
                             StandardOpenOption.TRUNCATE_EXISTING,
                             StandardOpenOption.WRITE),
                     streamBufferSize)) {

            byte[] buf = new byte[ioBufferSize];
            int n;
            while ((n = in.read(buf)) != -1) {
                out.write(buf, 0, n);
            }
            out.flush();
        }
    }

}