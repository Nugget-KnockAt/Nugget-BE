package com.example.nuggetbe.dto.response;

import lombok.*;

import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoginRes {
    String token;
    String email;
    UUID uuid;
}