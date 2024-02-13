package com.example.nuggetbe.service;

import com.example.nuggetbe.dto.request.EventDto;
import com.example.nuggetbe.dto.response.BaseException;
import com.example.nuggetbe.dto.response.EventDetailRes;
import com.example.nuggetbe.dto.response.EventsRes;
import com.example.nuggetbe.entity.Event;
import com.example.nuggetbe.entity.Member;
import com.example.nuggetbe.entity.Message;
import com.example.nuggetbe.repository.EventRepository;
import com.example.nuggetbe.repository.MemberRepository;
import com.example.nuggetbe.repository.MessageRepository;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class EventService {

    private final EventRepository eventRepository;
    private final MemberRepository memberRepository;
    private final MessageRepository messageRepository;
    public Event createEvent(EventDto eventDto, String email) {

        Event event = new Event();

        String text = actionToText(eventDto.getAction(), email);
        // 위도 경도를 주소
        String address = toAddress(eventDto);

        event.setMember(memberRepository.findByEmail(email));
        event.setText(text);
        event.setLocationInfo(address);
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

    public EventDetailRes readEvent(Long eventId) {
        Optional<Event> findEvent = eventRepository.findById(eventId);
        EventDetailRes eventDetailRes = new EventDetailRes();

        if (findEvent.isPresent()) {
            Event event = findEvent.get();

            eventDetailRes = EventDetailRes.builder()
                    .eventId(event.getId())
                    .locationInfo(event.getLocationInfo())
                    .memberName(event.getMember().getName())
                    .memberEmail(event.getMember().getEmail())
                    .createdAt(event.getCreatedAt())
                    .text(event.getText())
                    .build();
        }

        return eventDetailRes;
    }

    private String actionToText(String action, String email) {
        Member member = memberRepository.findByEmail(email);
        Message message =(Message) messageRepository.findByMemberAndAction(member, action).orElseThrow();

        String text = message.getText();

        return text;
    }

    private String toAddress(EventDto eventDto) {
        String apiKey = "AIzaSyCurCaBC7A7ZUHaMRZ9w8uKYixcHT6LH70";
        double latitude = eventDto.getLatitude();
        double longitude = eventDto.getLongitude();

        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey(apiKey)
                .build();

        try {
            LatLng latLng = new LatLng(latitude, longitude);
            GeocodingResult[] results = GeocodingApi.newRequest(context).latlng(latLng).await();

            System.out.println("results[0].formattedAddress = " + results[0].formattedAddress);

            return results[0].formattedAddress;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
    }
}
