package com.example.nuggetbe.dto.response;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FindUserResponse {
    String email;
    Role role;
}
