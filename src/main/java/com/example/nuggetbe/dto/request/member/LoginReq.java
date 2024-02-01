package com.example.nuggetbe.dto.request.member;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginReq {
    @JsonProperty
    @NotNull
    private String email;

    @JsonProperty
    @NotNull
    private String password;
}
