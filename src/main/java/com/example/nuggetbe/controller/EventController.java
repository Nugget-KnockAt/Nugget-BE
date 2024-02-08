package com.example.nuggetbe.controller;

import com.example.nuggetbe.dto.response.*;
import com.example.nuggetbe.service.SseEmitters;
import com.example.nuggetbe.dto.request.EventDto;
import com.example.nuggetbe.entity.Event;
import com.example.nuggetbe.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping(value = "/member")
public class EventController {

    private final EventService eventService;
    private final SseEmitters sseEmitters;

    @PostMapping("/event")
    public BaseResponse<?> createEvent(@RequestBody EventDto eventDto) {
        try {
            System.out.println("locationInfo = " + eventDto.getLocationInfo());

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String id = authentication.getName();

            Event event = eventService.createEvent(eventDto, id);

            // sse
            EventResponse eventResponse = sseEmitters.sentEvent(id, event);

            return new BaseResponse<>(BaseResponseStatus.SUCCESS, eventResponse);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @GetMapping("/event")
    public BaseResponse<?> readEvent(@RequestParam String email) {
        try {
            String memberEmail = email;

            List<EventsRes> eventsResList = eventService.readEvents(memberEmail);

            return new BaseResponse<>(BaseResponseStatus.SUCCESS, eventsResList);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
}
