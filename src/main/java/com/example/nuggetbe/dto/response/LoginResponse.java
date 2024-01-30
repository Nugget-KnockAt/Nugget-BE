package com.example.nuggetbe.dto.response;

import lombok.*;

import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoginResponse {
    String accessToken;
    String refreshToken;
    String email;
    UUID uuid;
    Role role;
}