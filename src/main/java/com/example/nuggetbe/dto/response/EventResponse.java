package com.example.nuggetbe.dto.response;

import com.example.nuggetbe.entity.Member;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EventResponse {

    private String eventLocation;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private List<String> guardianList;
}
