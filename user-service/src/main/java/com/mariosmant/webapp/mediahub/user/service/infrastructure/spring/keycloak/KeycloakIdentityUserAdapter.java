package com.mariosmant.webapp.mediahub.user.service.infrastructure.spring.keycloak;

import com.mariosmant.webapp.mediahub.user.service.domain.model.User;
import com.mariosmant.webapp.mediahub.user.service.domain.port.IdentityUserPort;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class KeycloakIdentityUserAdapter implements IdentityUserPort {

    private final Keycloak keycloak;
    private final KeycloakAdminProperties props;

    public KeycloakIdentityUserAdapter(Keycloak keycloak, KeycloakAdminProperties props) {
        this.keycloak = keycloak;
        this.props = props;
    }

    private RealmResource realm() {
        return keycloak.realm(props.getRealm());
    }

    private static User toDomain(UserRepresentation ur) {
        User u = new User();
        u.setId(ur.getId());
        u.setUsername(ur.getUsername());
        u.setEmail(ur.getEmail());
        u.setFirstName(ur.getFirstName());
        u.setLastName(ur.getLastName());
        u.setEnabled(Boolean.TRUE.equals(ur.isEnabled()));
        // Keycloak does not return createdAt by default; approximate with now if absent
        u.setCreatedAt(Instant.now());
        return u;
    }

    @Override
    public String create(User user) {
        UserRepresentation ur = new UserRepresentation();
        ur.setUsername(user.getUsername());
        ur.setEmail(user.getEmail());
        ur.setFirstName(user.getFirstName());
        ur.setLastName(user.getLastName());
        ur.setEnabled(user.isEnabled());

        Response response = realm().users().create(ur);
        if (response.getStatus() >= 200 && response.getStatus() < 300) {
            String location = response.getHeaderString("Location");
            String id = location != null ? location.substring(location.lastIndexOf('/') + 1) : null;
            response.close();
            if (id == null) throw new IllegalStateException("Failed to parse Keycloak user id");
            return id;
        } else {
            String msg = "Keycloak create user failed: HTTP " + response.getStatus();
            response.close();
            throw new IllegalStateException(msg);
        }
    }

    @Override
    public void update(String id, User user) {
        UserRepresentation ur = realm().users().get(id).toRepresentation();
        if (ur == null) throw new IllegalArgumentException("User not found: " + id);
        if (user.getEmail() != null) ur.setEmail(user.getEmail());
        if (user.getFirstName() != null) ur.setFirstName(user.getFirstName());
        if (user.getLastName() != null) ur.setLastName(user.getLastName());
        ur.setEnabled(user.isEnabled());
        realm().users().get(id).update(ur);
    }

    @Override
    public Optional<User> getById(String id) {
        try {
            UserRepresentation ur = realm().users().get(id).toRepresentation();
            if (ur == null) return Optional.empty();
            return Optional.of(toDomain(ur));
        } catch (jakarta.ws.rs.NotFoundException nf) {
            return Optional.empty();
        }
    }

    @Override
    public void delete(String id) {
        realm().users().get(id).remove();
    }

    @Override
    public List<User> search(String query, int offset, int limit) {
        List<UserRepresentation> reps = realm().users().search(query, offset, limit, true);
        return reps.stream().map(KeycloakIdentityUserAdapter::toDomain).collect(Collectors.toList());
    }
}
