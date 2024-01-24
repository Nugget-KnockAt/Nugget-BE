package com.example.nuggetbe.controller;

import com.example.nuggetbe.dto.request.LoginDto;
import com.example.nuggetbe.dto.request.SignUpDto;
import com.example.nuggetbe.dto.response.*;
import com.example.nuggetbe.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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
            CallbackResponse result = memberService.getKakaoToken(code);

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
