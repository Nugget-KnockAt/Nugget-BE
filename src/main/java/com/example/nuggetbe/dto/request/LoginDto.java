package com.example.nuggetbe.dto.request;

import com.example.nuggetbe.entity.KakaoOAuthToken;
import lombok.*;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.antlr.v4.runtime.misc.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class LoginDto {


    @JsonProperty
    @NotNull
    private Long id;


}

