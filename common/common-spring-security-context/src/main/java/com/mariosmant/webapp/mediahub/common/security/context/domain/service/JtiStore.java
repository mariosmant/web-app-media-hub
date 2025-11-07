package com.mariosmant.webapp.mediahub.common.security.context.domain.service;

public interface JtiStore {
    boolean seen(String jti);
    void remember(String jti);
}
