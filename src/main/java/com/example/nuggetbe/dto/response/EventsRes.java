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
    private String memberName;
    private String locationInfo;
    private LocalDateTime createdAt;

    private Double latitude;
    private Double longitude;
}
