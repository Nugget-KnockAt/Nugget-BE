package com.example.nuggetbe.controller;

import com.example.nuggetbe.dto.response.BaseException;
import com.example.nuggetbe.dto.response.BaseResponse;
import com.example.nuggetbe.dto.response.BaseResponseStatus;
import com.example.nuggetbe.dto.response.CallbackGoogleResponse;
import com.example.nuggetbe.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/login/oauth2", produces = "application/json")
public class LoginController {
    /*

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

     */
}
