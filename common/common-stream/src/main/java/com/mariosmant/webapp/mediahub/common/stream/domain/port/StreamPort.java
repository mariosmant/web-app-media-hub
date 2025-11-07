package com.mariosmant.webapp.mediahub.common.stream.domain.port;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.List;

public interface StreamPort {
    void readInputWriteOutputStream(InputStream inputStream, OutputStream outputStream, byte[] buffer, boolean shouldFlush) throws IOException;
}
