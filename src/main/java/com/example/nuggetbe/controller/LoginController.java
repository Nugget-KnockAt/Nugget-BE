package com.example.nuggetbe.controller;

import com.example.nuggetbe.dto.request.LoginDto;
import com.example.nuggetbe.dto.request.SignUpDto;
import com.example.nuggetbe.dto.response.*;
import com.example.nuggetbe.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RequestMapping(value = "/login/oauth2", produces = "application/json")
@RestController
public class LoginController {

    private final LoginService loginService;

    @GetMapping("/code/{registrationId}")
    public BaseResponse<?> googleLogin(@RequestParam("code") String code, @PathVariable("registrationId") String registrationId) {
        try {
            CallbackGoogleResponse result = loginService.socialLogin(code, registrationId);

            return new BaseResponse<>(BaseResponseStatus.SUCCESS, result);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @PostMapping("/google/signup")
    public BaseResponse<?> signup(@RequestBody @Valid SignUpDto signUpDto) {
        try {
            SignUpResponse response = loginService.signup(signUpDto);

            return new BaseResponse<>(BaseResponseStatus.SUCCESS, response);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @PostMapping("/google/login")
    public BaseResponse<?> login(@RequestBody LoginDto loginDto) {
        try {
            LoginResponse response = loginService.login(loginDto);

            return new BaseResponse<>(BaseResponseStatus.SUCCESS, response);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
}
