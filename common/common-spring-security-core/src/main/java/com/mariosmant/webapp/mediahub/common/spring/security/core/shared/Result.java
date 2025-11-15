package com.mariosmant.webapp.mediahub.common.spring.security.core.shared;

import org.springframework.security.oauth2.core.OAuth2Error;

public sealed interface Result permits Result.Success, Result.Failure {
    record Success() implements Result {}
    record Failure(OAuth2Error error) implements Result {}
}

