package com.mariosmant.webapp.mediahub.common.file.spring.domain.port;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface SpringFilePort {
    void multipartFileToFile(MultipartFile multipartFile, Path destination) throws IOException;
}
