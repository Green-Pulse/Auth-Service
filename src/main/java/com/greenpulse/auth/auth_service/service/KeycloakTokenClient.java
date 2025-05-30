package com.greenpulse.auth.auth_service.service;

import org.glassfish.jersey.jackson.JacksonFeature;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.MediaType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class KeycloakTokenClient {

    @Value("${keycloak.auth-server-url}")
    private String authServerUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.resource}")
    private String clientId;

    @Value("${keycloak.credentials.secret}")
    private String clientSecret;

    public Map<String, Object> getToken(String username, String password) {
        Client client = ClientBuilder.newBuilder()
                .register(JacksonFeature.class)
                .build();

        Form form = new Form();
        form.param("grant_type", "password");
        form.param("client_id", clientId);
        form.param("client_secret", clientSecret);
        form.param("username", username);
        form.param("password", password);

        return client
                .target(authServerUrl + "/realms/" + realm + "/protocol/openid-connect/token")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.form(form), Map.class);
    }
}
