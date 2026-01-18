package com.mariosmant.webapp.mediahub.common.spring.security.core.infrastructure.spring.properties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class ClaimSubjectPolicy {
    private boolean subjectIsUser;
    private boolean subjectIsServiceAccount;
    private String userSubjectPattern;
    private Set<String> serviceAccountSubjectClientIds = new HashSet<>();
    private String serviceAccountSubjectPattern;
}
