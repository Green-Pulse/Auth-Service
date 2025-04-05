package com.greenpulse.auth.auth_service.dto;


public record RegisterRequest(
        String username,
        String password,
        String email
) {}