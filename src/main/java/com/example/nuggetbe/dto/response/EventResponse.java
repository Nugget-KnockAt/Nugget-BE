package com.example.nuggetbe.dto.response;

import com.example.nuggetbe.entity.Member;
import lombok.*;

import java.util.List;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EventResponse {

    private String eventLocation;
    private List<String> guardianList;
}
