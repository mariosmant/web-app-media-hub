package com.mariosmant.webapp.mediahub.common.stream.infrastructure.file;

import com.mariosmant.webapp.mediahub.common.stream.domain.port.StreamPort;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class StreamService implements StreamPort {

    @Override
    public void readInputWriteOutputStream(InputStream inputStream, OutputStream outputStream, byte[] buffer, boolean shouldFlush) throws IOException {
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        if(shouldFlush) {
            outputStream.flush();
        }
    }

}