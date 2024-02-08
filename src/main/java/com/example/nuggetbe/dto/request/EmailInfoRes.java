package com.example.nuggetbe.dto.request;

import com.example.nuggetbe.entity.Role;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmailInfoRes {

    private UUID uuid;
    private String email;
    private String name;
    private String phoneNumber;
}
