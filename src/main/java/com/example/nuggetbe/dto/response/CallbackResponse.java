package com.example.nuggetbe.dto.response;

import com.example.nuggetbe.entity.Role;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CallbackResponse {
    private Long id;
    private Role role;
}
