package com.example.nuggetbe.controller;

import com.example.nuggetbe.service.SseEmitters;
import com.example.nuggetbe.dto.response.BaseException;
import com.example.nuggetbe.dto.response.BaseResponse;
import com.example.nuggetbe.dto.response.BaseResponseStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@RestController
public class SseController {

    private final SseEmitters sseEmitters;

    @GetMapping(value = "/member/sse-connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public BaseResponse<?> connect() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();

            SseEmitter emitter = new SseEmitter(60 * 1000L);
            sseEmitters.handleSse(userEmail, emitter);

            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("connect success"));
            return new BaseResponse<>(BaseResponseStatus.SUCCESS, "이벤트 알림 요청 수락");
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
