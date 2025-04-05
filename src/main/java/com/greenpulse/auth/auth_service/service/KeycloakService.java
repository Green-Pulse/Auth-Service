package com.greenpulse.auth.auth_service.service;


import com.greenpulse.auth.auth_service.dto.LoginRequest;
import com.greenpulse.auth.auth_service.dto.RegisterRequest;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class KeycloakService {

    private final Keycloak keycloak;
    private final String realm;
    private final KeycloakTokenClient tokenClient;

    public KeycloakService(Keycloak keycloak,
                           @Value("${keycloak.realm}") String realm,
                           KeycloakTokenClient tokenClient) {
        this.keycloak = keycloak;
        this.realm = realm;
        this.tokenClient = tokenClient;
    }

    public void registerUser(RegisterRequest request) {
        UsersResource usersResource = keycloak.realm(realm).users();

        UserRepresentation user = new UserRepresentation();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setEnabled(true);

        CredentialRepresentation passwordCred = new CredentialRepresentation();
        passwordCred.setTemporary(false);
        passwordCred.setType(CredentialRepresentation.PASSWORD);
        passwordCred.setValue(request.password());

        user.setCredentials(List.of(passwordCred));

        Response response = usersResource.create(user);
        if (response.getStatus() != 201) {
            throw new RuntimeException("Failed to register user: " + response.getStatus());
        }
    }

    public Map<String, Object> getToken(LoginRequest request) {
        return tokenClient.getToken(request.username(), request.password());
    }
}