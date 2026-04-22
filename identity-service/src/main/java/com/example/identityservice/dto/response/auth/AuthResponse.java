package com.example.identityservice.dto.response.auth;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthResponse {
    String accessToken;
    String refreshToken;

    boolean isAuthenticated;

    @Data
    @Builder
    public static class UserSummary {
        String username;
        Set<String> roles;
        Set<String> permissions;
    }
}
