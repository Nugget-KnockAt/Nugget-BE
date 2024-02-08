package com.example.nuggetbe.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class EventDto {

    @JsonProperty
    @NotNull
    private String locationInfo;

    @JsonProperty
    private BigDecimal latitude;

    @JsonProperty
    private BigDecimal longitude;
}
