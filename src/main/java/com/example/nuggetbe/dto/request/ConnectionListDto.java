package com.example.nuggetbe.dto.request;

import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConnectionListDto {

    private List<String> connectionList;
}
