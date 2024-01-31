package com.example.nuggetbe.controller;

import com.example.nuggetbe.dto.request.KakaoNicknameDto;
import com.example.nuggetbe.dto.request.LoginDto;
import com.example.nuggetbe.dto.request.SignUpDto;
import com.example.nuggetbe.dto.response.*;
import com.example.nuggetbe.service.KakaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(value = "/login/oauth2")
public class KakaoController {
    private final KakaoService memberService;

    @PostMapping("/kakao")
    public BaseResponse<?> kakaoCallback(@RequestBody KakaoNicknameDto nickname) {
        try {
            //nickname받으면 회원 정보 return하는걸로 수정

            CallbackResponse result = memberService.getKakaoToken(nickname.getNickname());

            return new BaseResponse<>(BaseResponseStatus.SUCCESS, result);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @PostMapping("/signUp")
    public BaseResponse<?> signUp(@RequestBody @Valid SignUpDto signUpDto) {
        try {
            SignUpResponse result =  memberService.signUp(signUpDto);
            return new BaseResponse<>(BaseResponseStatus.SUCCESS, result);
        } catch (BaseException e) {
            return new BaseResponse(e.getStatus());
        }
    }

    @PostMapping("/login")
    public BaseResponse<?> login(@RequestBody LoginDto loginDto) {
        try {
            LoginResponse loginRes = memberService.login(loginDto);
            return new BaseResponse<>(BaseResponseStatus.SUCCESS, loginRes);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
}
