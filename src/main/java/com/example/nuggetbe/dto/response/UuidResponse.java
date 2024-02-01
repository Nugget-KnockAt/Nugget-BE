package com.example.nuggetbe.dto.response;

import lombok.*;

import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UuidResponse {
    private UUID uuid;
}
