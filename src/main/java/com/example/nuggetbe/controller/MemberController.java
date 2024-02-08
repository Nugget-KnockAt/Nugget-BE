package com.example.nuggetbe.controller;


import com.example.nuggetbe.dto.request.EmailInfoReq;
import com.example.nuggetbe.dto.request.ConnectionDto;
import com.example.nuggetbe.dto.request.CustomTouchPostDto;
import com.example.nuggetbe.dto.request.EmailInfoRes;
import com.example.nuggetbe.dto.request.member.LoginReq;
import com.example.nuggetbe.dto.request.member.SignupReq;
import com.example.nuggetbe.dto.response.*;
import com.example.nuggetbe.dto.response.member.LoginRes;
import com.example.nuggetbe.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(value = "/member")
public class MemberController {
    @Autowired
    private MemberService memberService;

    @PostMapping("/signUp")
    public BaseResponse<?> signUp(@RequestBody @Valid SignupReq signupReq) {
        try {
            LoginRes result =  memberService.signUp(signupReq);
            return new BaseResponse<>(BaseResponseStatus.SUCCESS, result);
        } catch (BaseException e) {
            return new BaseResponse(e.getStatus());
        }
    }

    @GetMapping("/info")
    public BaseResponse<?> info(@RequestBody EmailInfoReq emailInfoReq) {
        try {
            EmailInfoRes result = memberService.getUserInfo(emailInfoReq);
            return new BaseResponse<>(BaseResponseStatus.SUCCESS, result);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @PostMapping("/login")
    public BaseResponse<?> login(@RequestBody LoginReq loginReq) {
        try {
            LoginRes loginRes = memberService.login(loginReq);
            return new BaseResponse<>(BaseResponseStatus.SUCCESS, loginRes);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @GetMapping("/checkEmail")
    public BaseResponse<?> checkEmail(@RequestParam String email) {
        try {
            Boolean notDuplicate = memberService.checkEmail(email);
            if (notDuplicate.equals(true)) {
                return new BaseResponse<>(BaseResponseStatus.SUCCESS, true);
            }
            return new BaseResponse<>(BaseResponseStatus.DUPLICATE_EMAIL, false);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @PostMapping("/connect")
    public BaseResponse<?> createConnection(@RequestBody ConnectionDto request) {
        try {
            UUID uuid = UUID.fromString(request.getUuid());
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String memberId = authentication.getName();
                memberService.createConnection(uuid, memberId);
            return new BaseResponse<>(BaseResponseStatus.SUCCESS, "연결 성공");
        } catch (IllegalArgumentException e) {
            log.error("Error in createConnection: Invalid UUID string", e);
            return new BaseResponse<>(BaseResponseStatus.INVALID_UUID_FORMAT, "Invalid UUID format");
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @PostMapping("/customTouch")
    public BaseResponse<?> customTouch(@RequestBody CustomTouchPostDto customTouchPostDto) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            System.out.println(authentication);
            System.out.println(authentication.getName());
            String memberId = authentication.getName();
            memberService.saveCustomTouch(customTouchPostDto, memberId);
            return new BaseResponse<>(BaseResponseStatus.SUCCESS, "커스텀 터치 저장 성공");
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
    @GetMapping("/customTouch/{touchCount}")
    public BaseResponse<?> getCustomTouch(@PathVariable int touchCount) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String memberId = authentication.getName();
            GetCustomTouchResponse getCustomTouchResponse = memberService.getCustomTouch(touchCount, memberId);
            return new BaseResponse<>(BaseResponseStatus.SUCCESS, getCustomTouchResponse);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @GetMapping("/deleteMember")
    public BaseResponse<?> deleteMember() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String memberId = authentication.getName();
            memberService.deleteMember(memberId);
            return new BaseResponse<>(BaseResponseStatus.SUCCESS, "회원 탈퇴 성공");
        } catch (BaseException e) {
            return new BaseResponse<>(BaseResponseStatus.FAILED_TO_DELETE_MEMBER, "회원 탈퇴 실패");
        }
    }

    @GetMapping("/uuid")
    public BaseResponse<?> getUuid() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String memberId = authentication.getName();
            UUID uuid = memberService.getUuid(memberId);
            UuidResponse uuidResponse = new UuidResponse(uuid);
            return new BaseResponse<>(BaseResponseStatus.SUCCESS, uuidResponse);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

}
