package com.example.nuggetbe.controller;


import com.example.nuggetbe.dto.request.ConnectionDto;
import com.example.nuggetbe.dto.request.CustomTouchPostDto;
import com.example.nuggetbe.dto.request.LoginDto;
import com.example.nuggetbe.dto.response.*;
import com.example.nuggetbe.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.apache.commons.lang3.StringUtils.isNumeric;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(value = "/member")
public class MemberController {
    @Autowired
    private MemberService memberService;

    @PostMapping("/connect")
    public BaseResponse<?> createConnection(@RequestBody ConnectionDto request) {
        try {
            UUID uuid = UUID.fromString(request.getUuid());
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                Long memberId = Long.valueOf(authentication.getName());
                memberService.createConnection(uuid, memberId);
            }
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
            if (authentication != null && authentication.isAuthenticated()) {
                Long memberId = Long.valueOf(authentication.getName());
                memberService.saveCustomTouch(customTouchPostDto, memberId);
                return new BaseResponse<>(BaseResponseStatus.SUCCESS, "커스텀 터치 저장 성공");
            }
            return new BaseResponse<>(BaseResponseStatus.FAILED_TO_SAVE_CUSTOM_TOUCH);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
    @GetMapping("/customTouch/{touchCount}")
    public BaseResponse<?> getCustomTouch(@PathVariable int touchCount) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            System.out.println(authentication);
            System.out.println(authentication.getName());
            if (authentication != null && authentication.isAuthenticated()) {
                Long memberId = Long.valueOf(authentication.getName());
                GetCustomTouchResponse getCustomTouchResponse = memberService.getCustomTouch(touchCount, memberId);
                return new BaseResponse<>(BaseResponseStatus.SUCCESS, getCustomTouchResponse);
            } return new BaseResponse<>(BaseResponseStatus.FAILED_TO_GET_CUSTOM_TOUCH);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @PostMapping("/deleteMember")
    public BaseResponse<?> deleteMember(@RequestBody LoginDto loginDto) {
        try {
             Long memberId = loginDto.getId();
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
            Long id = Long.valueOf(authentication.getName());
            UUID uuid = memberService.getUuid(id);
            UuidResponse uuidResponse = new UuidResponse(uuid);
            return new BaseResponse<>(BaseResponseStatus.SUCCESS, uuidResponse);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

}
