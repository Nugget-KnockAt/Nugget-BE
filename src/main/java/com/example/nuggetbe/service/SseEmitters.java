package com.example.nuggetbe.service;

import com.example.nuggetbe.dto.response.EventResponse;
import com.example.nuggetbe.entity.Connection;
import com.example.nuggetbe.entity.Member;
import com.example.nuggetbe.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
@Component
public class SseEmitters {

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final MemberRepository memberRepository;

    public SseEmitter handleSse(String userEmail, SseEmitter emitter) {
        this.emitters.put(userEmail, emitter);

        log.info("new emitter added {} = {}", userEmail, emitter);
        log.info("emitter list size = {}", emitters.size());

        emitter.onCompletion(() -> {
            log.info("onCompletion callback");
            this.emitters.remove(userEmail);
        });

        emitter.onTimeout(() -> {
            log.info("onTimeout callback");
            emitter.complete();
        });

        return emitter;
    }

    public EventResponse sentEvent(String userEmail, String locationInfo) {
        Member member = memberRepository.findByEmail(userEmail);

        List<Connection> connections = member.getConnections();
        List<Member> guardianList = connections.stream()
                .map(Connection::getGuardian)
                .toList();

        List<SseEmitter> guardianEmitters = guardianList.stream()
                .map(guardian -> emitters.get(guardian.getEmail()))
                .filter(Objects::nonNull)
                .toList();

        String jsonData = "{\"event\": \"피보호자에게" + locationInfo + "에서 이벤트가 발생했습니다.\"}";
        guardianEmitters.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event()
                        .data(jsonData, MediaType.APPLICATION_JSON));
            } catch (IOException e) {
                emitter.completeWithError(e);
            }
        });

        return EventResponse.builder()
                .guardianList(guardianList)
                .eventLocation(locationInfo)
                .build();
    }
}
