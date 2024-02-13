package com.example.nuggetbe.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventsRes {

    private Long eventId;
    private String memberEmail;
    private String locationInfo;
    private LocalDateTime createdAt;

    private double latitude;
    private double longitude;
}
