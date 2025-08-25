package com.mariosmant.webapp.mediahub.user.service.domain.port;


import com.mariosmant.webapp.mediahub.user.service.domain.model.User;

import java.util.List;
import java.util.Optional;

public interface IdentityUserPort {
    String create(User user);
    void update(String id, User user);
    Optional<User> getById(String id);
    void delete(String id);
    List<User> search(String query, int offset, int limit);
}

