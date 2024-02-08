package com.example.nuggetbe.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CustomTouchPostDto {
    @JsonProperty
    private String action;

    @JsonProperty
    private String text;


}
