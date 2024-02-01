package com.example.nuggetbe.dto.response.member;

import com.example.nuggetbe.entity.Role;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginRes {
    private String accessToken;
    private String refreshToken;
    private String email;
    private String name;
    private String phoneNumber;
    private Role role;
}
