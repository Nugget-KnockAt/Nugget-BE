package com.example.nuggetbe.dto.request;

import com.example.nuggetbe.entity.SocialLogin;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class KakaoDto {
    @JsonProperty
    @NotNull
    private Long socialId;
    @JsonProperty
    @NotNull
    private SocialLogin socialLogin;
    @JsonProperty
    @NotNull
    private String nickname;
}
