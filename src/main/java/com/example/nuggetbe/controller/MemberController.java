package com.example.nuggetbe.controller;

import com.example.nuggetbe.dto.response.BaseException;
import com.example.nuggetbe.dto.response.BaseResponse;
import com.example.nuggetbe.dto.response.BaseResponseStatus;
import com.example.nuggetbe.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(value = "/member")
public class MemberController {
    @Autowired
    private MemberService memberService;

    @PostMapping("/connect")
    public BaseResponse<?> createConnection(@RequestParam String uuid) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String id = authentication.getName();

            memberService.createConnection(uuid, id);
            return new BaseResponse<>(BaseResponseStatus.SUCCESS, "연결 성공");
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
}
