package com.example.nuggetbe.dto.response;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CallbackGoogleResponse {
    private Long id;
    private String email;
    private Role role;
}
