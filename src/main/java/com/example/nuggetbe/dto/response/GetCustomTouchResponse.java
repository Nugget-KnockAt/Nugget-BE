package com.example.nuggetbe.dto.response;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetCustomTouchResponse {
    private String action;
    private String text;

}
