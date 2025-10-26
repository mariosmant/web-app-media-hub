package com.mariosmant.webapp.mediahub.common.file.core.domain.port;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface FilePort {
    void copyFileToFile(Path source, Path destination) throws IOException;
    void zipFiles(List<Path> sources, Path destination) throws IOException;
}
