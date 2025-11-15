package com.mariosmant.webapp.mediahub.common.file.azure.fileshare.domain.port;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;

public interface AzureFileShareFilePort {
    void copyFileShareFileToFile(Path source, Path destination) throws IOException;
    void copyFileToFileShareFile(Path source, Path destination) throws IOException;
    void zipFilesToFileShareFile(List<Path> sources, Path destination) throws IOException;
    void zipFileShareFilesToFile(List<Path> sources, Path destination) throws IOException;
    void zipFileShareFilesToFileShareFile(List<Path> sources, Path destination) throws IOException;
    void writeInputStreamToFileShareFile(InputStream inputStream, Path destination) throws IOException;
}
