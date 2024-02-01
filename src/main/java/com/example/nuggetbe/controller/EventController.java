package com.example.nuggetbe.controller;

import com.example.nuggetbe.dto.request.EventDto;
import com.example.nuggetbe.dto.response.BaseException;
import com.example.nuggetbe.dto.response.BaseResponse;
import com.example.nuggetbe.dto.response.BaseResponseStatus;
import com.example.nuggetbe.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.web.JsonPath;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping(value = "/member")
public class EventController {

    private final EventService eventService;

    @PostMapping("/event")
    public BaseResponse<?> createEvent(@RequestBody EventDto eventDto) {
        try {
            System.out.println("locationInfo = " + eventDto.getLocationInfo());

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String id = authentication.getName();

            eventService.createEvent(eventDto.getLocationInfo(), id);
            return new BaseResponse<>(BaseResponseStatus.SUCCESS);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();
    @GetMapping("/notify")
    public SseEmitter notifyUser() {
        SseEmitter emitter = new SseEmitter();
        this.emitters.add(emitter);

        emitter.onCompletion(() -> this.emitters.remove(emitter));
        emitter.onTimeout(() -> this.emitters.remove(emitter));

        return emitter;
    }

    public void sendEventToClients(String location, String eventTime) {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().data("Location: " + location + ", Time: " + eventTime));
            } catch (IOException e) {
                emitters.remove(emitter);
            }
        }
    }
}
