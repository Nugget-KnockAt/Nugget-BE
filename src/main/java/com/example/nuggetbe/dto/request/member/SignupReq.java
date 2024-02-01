package com.example.nuggetbe.dto.request.member;

import com.example.nuggetbe.entity.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SignupReq {
    @JsonProperty
    @NotNull
    private String email;
    @JsonProperty
    @NotNull
    private String password;
    @JsonProperty
    @NotNull
    private String name;
    @JsonProperty
    @NotNull
    private String phoneNumber;
    @JsonProperty
    @NotNull
    private Role role;

}
