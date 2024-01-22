package com.example.nuggetbe.controller;

import com.example.nuggetbe.dto.request.KakaoSignUpOAuthDto;
import com.example.nuggetbe.dto.request.LoginDto;
import com.example.nuggetbe.dto.response.BaseException;
import com.example.nuggetbe.dto.response.BaseResponse;
import com.example.nuggetbe.dto.response.BaseResponseStatus;
import com.example.nuggetbe.dto.response.LoginRes;
import com.example.nuggetbe.entity.KakaoOAuthToken;
import com.example.nuggetbe.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(value = "/login/oauth2")
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/kakao/callback")
    public BaseResponse<?> kakaoCallback(String code) {
        try {
            //code 이용 하여 oAuthAccessToken 얻어옴
            KakaoOAuthToken kaKaoOAuthToken = memberService.getKakaoToken(code);
            //oAuthAccessToken 으로 nickname 가져옴
            String nickname = memberService.getOAuthInfo(kaKaoOAuthToken);
            // 해당 nickname 으로 된 계정이 있는지 확인
            Boolean notDuplicate = memberService.checkEmail(nickname);
            // 없다면 회원가입 후 로그인
            if (notDuplicate.equals(true)) {
                KakaoSignUpOAuthDto signUpOAuthDto = new KakaoSignUpOAuthDto();
                signUpOAuthDto.setName(nickname);
                signUpOAuthDto.setEmail(nickname);
                memberService.signUpOAUth(signUpOAuthDto);
                LoginDto loginDto = new LoginDto();
                loginDto.setEmail(nickname);
                loginDto.setPassword("12345");
                LoginRes loginRes = memberService.login(loginDto);
                return new BaseResponse<>(BaseResponseStatus.SUCCESS, loginRes);
            }
            // 있다면 로그인
            LoginDto loginDto = new LoginDto();
            loginDto.setEmail(nickname);
            loginDto.setPassword("12345");
            LoginRes loginRes = memberService.login(loginDto);
            return new BaseResponse<>(BaseResponseStatus.SUCCESS, loginRes);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
}
