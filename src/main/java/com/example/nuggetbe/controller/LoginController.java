package com.example.nuggetbe.controller;

import com.example.nuggetbe.service.LoginService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/login/oauth2", produces = "application/json")
public class LoginController {

    LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @GetMapping("/code/{registrationId}")
    public void googleLogin(@RequestParam("code") String code, @PathVariable("registrationId") String registrationId) {
        loginService.socialLogin(code, registrationId);
    }
}
