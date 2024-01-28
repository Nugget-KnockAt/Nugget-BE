package com.example.nuggetbe.controller;

import com.example.nuggetbe.dto.response.BaseException;
import com.example.nuggetbe.dto.response.BaseResponse;
import com.example.nuggetbe.dto.response.BaseResponseStatus;
import com.example.nuggetbe.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping(value = "/member")
public class EventController {

    private final EventService eventService;

    @PostMapping("/event")
    public BaseResponse<?> createEvent(@RequestBody String locationInfo) {
        try {
            System.out.println("locationInfo = " + locationInfo);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String id = authentication.getName();

            eventService.createEvent(locationInfo, id);
            return new BaseResponse<>(BaseResponseStatus.SUCCESS);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
}
