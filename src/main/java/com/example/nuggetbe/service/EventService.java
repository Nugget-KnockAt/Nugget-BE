package com.example.nuggetbe.service;

import com.example.nuggetbe.entity.Event;
import com.example.nuggetbe.repository.EventRepository;
import com.example.nuggetbe.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class EventService {

    private final EventRepository eventRepository;
    private final MemberRepository memberRepository;
    public Event createEvent(String locationInfo, String email) {

        Event event = new Event();

        event.setMember(memberRepository.findByEmail(email));
        event.setLocationInfo(locationInfo);
        event.setCreatedAt(LocalDateTime.now());

        eventRepository.save(event);

        return event;
    }
}
