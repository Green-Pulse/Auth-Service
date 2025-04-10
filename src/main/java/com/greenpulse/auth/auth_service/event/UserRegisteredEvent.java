package com.greenpulse.auth.auth_service.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class UserRegisteredEvent {
    private String username;
    private String email;
}
