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

    private BigDecimal latitude;    // 위도
    private BigDecimal longitude;   // 경도
}
