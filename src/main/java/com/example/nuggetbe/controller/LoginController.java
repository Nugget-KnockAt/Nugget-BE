package com.example.nuggetbe.controller;

import com.example.nuggetbe.dto.response.*;
import com.example.nuggetbe.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
}
