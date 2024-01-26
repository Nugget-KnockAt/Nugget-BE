package com.example.nuggetbe.controller;


import com.example.nuggetbe.dto.request.CustomTouchPostDto;
import com.example.nuggetbe.dto.response.BaseException;
import com.example.nuggetbe.dto.response.BaseResponse;
import com.example.nuggetbe.dto.response.BaseResponseStatus;
import com.example.nuggetbe.dto.response.GetCustomTouchResponse;
import com.example.nuggetbe.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
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
    public BaseResponse<?> createConnection(@RequestBody String uuidString) {
        UUID uuid = UUID.fromString(uuidString);
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null && authentication.isAuthenticated()) {
                Long memberId = Long.valueOf(authentication.getName());
                memberService.createConnection(uuid, memberId);
            }

            return new BaseResponse<>(BaseResponseStatus.SUCCESS, "연결 성공");
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

}
