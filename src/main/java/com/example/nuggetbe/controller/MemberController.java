package com.example.nuggetbe.controller;


import com.example.nuggetbe.dto.request.ConnectionDto;
import com.example.nuggetbe.dto.request.ConnectionListDto;
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

import java.util.List;
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
    public BaseResponse<?> info(@RequestParam String email) {
        try {
            EmailInfoRes result = memberService.getUserInfo(email);
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

    @GetMapping("/connect")
    public BaseResponse<?> connections() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String memberEmail = authentication.getName();

            ConnectionListDto response = memberService.getConnectionList(memberEmail);
            return new BaseResponse<>(BaseResponseStatus.SUCCESS, response);
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
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String guardianId = authentication.getName();
                memberService.createConnection(request.getEmail(), guardianId);
            return new BaseResponse<>(BaseResponseStatus.SUCCESS, "연결 성공");
        } catch (IllegalArgumentException e) {
            log.error("Error in createConnection: Invalid email string", e);
            return new BaseResponse<>(BaseResponseStatus.INVALID_UUID_FORMAT, "Invalid email format");
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
    @GetMapping("/customTouch")
    public BaseResponse<List<GetCustomTouchesResponse>> getCustomTouches() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String memberId = authentication.getName();
            List<GetCustomTouchesResponse> getCustomTouchesResponses = memberService.getCustomTouches(memberId);
            return new BaseResponse<>(BaseResponseStatus.SUCCESS, getCustomTouchesResponses);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @GetMapping("/customTouch/{action}")
    public BaseResponse<?> getCustomTouch(@PathVariable String action) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String memberId = authentication.getName();
            GetCustomTouchResponse getCustomTouchResponse = memberService.getCustomTouch(action, memberId);
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
