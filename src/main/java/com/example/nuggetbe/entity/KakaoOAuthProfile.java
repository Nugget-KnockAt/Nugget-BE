package com.example.nuggetbe.entity;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class KakaoOAuthProfile {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("setPrivacyInfo")
    private Boolean setPrivacyInfo;

    @JsonProperty("connected_at")
    private String connectedAt;

    @JsonProperty("properties")
    private Properties properties;

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    @Data
    public static class Properties {
        @JsonProperty("nickname")
        private String nickname;

    }

    @Data
    public static class KakaoAccount {
        @JsonProperty("profile_nickname_needs_agreement")
        private boolean profileNicknameNeedsAgreement;

        @JsonProperty("profile")
        private KakaoProfile kakaoProfile;
    }

    @Data
    public static class KakaoProfile {
        @JsonProperty("nickname")
        private String nickname;

    }
}