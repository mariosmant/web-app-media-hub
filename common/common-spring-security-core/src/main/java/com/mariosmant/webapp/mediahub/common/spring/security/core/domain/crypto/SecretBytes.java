package com.mariosmant.webapp.mediahub.common.spring.security.core.domain.crypto;

import java.util.Arrays;

/**
 * Holds sensitive bytes and zeroes them on close to reduce exposure windows.
 */
public final class SecretBytes implements AutoCloseable {
    private byte[] bytes;
    private boolean closed;

    public SecretBytes(byte[] bytes) {
        this.bytes = bytes == null ? new byte[0] : bytes.clone();
        this.closed = false;
    }

    public byte[] use() {
        if (closed) throw new IllegalStateException("SecretBytes closed");
        return bytes;
    }

    @Override
    public void close() {
        if (!closed && bytes != null) {
            Arrays.fill(bytes, (byte) 0);
            closed = true;
        }
    }
}

