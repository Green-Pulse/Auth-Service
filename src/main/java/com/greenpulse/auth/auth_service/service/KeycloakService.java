package com.greenpulse.auth.auth_service.service;


import com.greenpulse.auth.auth_service.dto.LoginRequest;
import com.greenpulse.auth.auth_service.dto.RegisterRequest;
import com.greenpulse.auth.auth_service.event.UserRegisteredEvent;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class KeycloakService {

    private final Keycloak keycloak;
    private final String realm;
    private final KeycloakTokenClient tokenClient;
    private final KafkaTemplate<String, UserRegisteredEvent> kafkaTemplate;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public KeycloakService(Keycloak keycloak,
                           @Value("${keycloak.realm}") String realm,
                           KeycloakTokenClient tokenClient, KafkaTemplate<String, UserRegisteredEvent> kafkaTemplate) {
        this.keycloak = keycloak;
        this.realm = realm;
        this.tokenClient = tokenClient;
        this.kafkaTemplate = kafkaTemplate;
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

        UserRegisteredEvent userRegisteredEvent = new UserRegisteredEvent(request.username(), request.email());
        CompletableFuture<SendResult<String, UserRegisteredEvent>> future
                = kafkaTemplate.send("user-registered-event-topic", request.username(), userRegisteredEvent);

        future.whenComplete((result, exception) -> {
            if (exception != null) {
                LOGGER.error("Failed to send user registered event: " + exception.getMessage());
            } else {
                LOGGER.info("User registered event sent successfully: " + result.getRecordMetadata());
            }

            LOGGER.info(("Partition: " + result.getRecordMetadata().partition()));
            LOGGER.info(("Topic: " + result.getRecordMetadata().topic()));
            LOGGER.info(("Offset: " + result.getRecordMetadata().offset()));

        });

//        future.join(); for sync but we don't need that I think
    }

    public Map<String, Object> getToken(LoginRequest request) {
        return tokenClient.getToken(request.username(), request.password());
    }
}