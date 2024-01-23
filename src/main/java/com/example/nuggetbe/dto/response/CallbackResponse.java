package com.example.nuggetbe.dto.response;

import com.example.nuggetbe.entity.KakaoOAuthToken;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CallbackResponse {
    private Long id;
    private Boolean isSignedUp;
}
