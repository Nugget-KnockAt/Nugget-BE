package com.example.nuggetbe.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventDetailRes {

    private Long eventId;
    private String locationInfo;
    private String memberName;
    private String memberEmail;
    private Double latitude;
    private Double longitude;
    private LocalDateTime createdAt;
    private String text;
}
