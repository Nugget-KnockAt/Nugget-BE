package com.example.nuggetbe.service;

import com.example.nuggetbe.dto.response.EventsRes;
import com.example.nuggetbe.entity.Event;
import com.example.nuggetbe.entity.Member;
import com.example.nuggetbe.repository.EventRepository;
import com.example.nuggetbe.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

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

    public List<EventsRes> readEvents(String memberEmail) {

        // 7일전
        LocalDateTime timeBefore7Days = LocalDateTime.now().minus(7, ChronoUnit.DAYS);
        Member member = memberRepository.findByEmail(memberEmail);

        List<Event> eventList = eventRepository.findAllByMemberAndCreatedAtAfter(member, timeBefore7Days);
        List<EventsRes> eventsResList = new ArrayList<>();

        eventList.forEach(event -> {
            EventsRes eventsRes = EventsRes.builder()
                    .eventId(event.getId())
                    .memberEmail(event.getMember().getEmail())
                    .locationInfo(event.getLocationInfo())
                    .createdAt(event.getCreatedAt())
                    .build();

            eventsResList.add(eventsRes);
        });

        return eventsResList;
    }
}
