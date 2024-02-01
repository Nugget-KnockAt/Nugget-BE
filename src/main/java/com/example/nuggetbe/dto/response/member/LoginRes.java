package com.example.nuggetbe.dto.response.member;

import com.example.nuggetbe.entity.Role;
import lombok.*;

import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginRes {
    private String accessToken;
    private String refreshToken;
    private UUID uuid;
    private String email;
    private String name;
    private String phoneNumber;
    private Role role;
}
