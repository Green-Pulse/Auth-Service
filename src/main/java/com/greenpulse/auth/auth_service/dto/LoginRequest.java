package com.greenpulse.auth.auth_service.dto;


public record LoginRequest(
        String username,
        String password
) {}