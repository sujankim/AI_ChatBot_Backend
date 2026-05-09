package com.sujan.chatbot.backend.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private String accessToken;
    private String tokenType;       // Always "Bearer"
    private long expiresIn;         // Seconds until access token expires
    private UserResponse user;      // Basic user info
}
