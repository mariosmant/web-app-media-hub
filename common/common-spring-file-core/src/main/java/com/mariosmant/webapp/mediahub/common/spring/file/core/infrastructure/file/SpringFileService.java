package com.mariosmant.webapp.mediahub.common.file.spring.infrastructure.file;

import com.mariosmant.webapp.mediahub.common.file.core.domain.port.FilePort;
import com.mariosmant.webapp.mediahub.common.file.spring.domain.port.SpringFilePort;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;

public class SpringFileService extends FileService implements SpringFilePort {

    /**
     * Convenience method: accepts a MultipartFile and delegates
     * to the InputStream-based method.
     */
    @Override
    public void multipartFileToFile(MultipartFile multipartFile, Path destination) throws IOException {
        try (InputStream in = multipartFile.getInputStream()) {
            filePort.writeInputStreamToFile(in, destination);
        }
    }
}
