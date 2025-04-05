package com.greenpulse.auth.auth_service.controller;


import com.greenpulse.auth.auth_service.dto.LoginRequest;
import com.greenpulse.auth.auth_service.dto.RegisterRequest;
import com.greenpulse.auth.auth_service.service.KeycloakService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final KeycloakService keycloakService;

    public AuthController(KeycloakService keycloakService) {
        this.keycloakService = keycloakService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        keycloakService.registerUser(request);
        return ResponseEntity.ok("User registered successfully!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        var token = keycloakService.getToken(request);
        return ResponseEntity.ok(token);
    }
}