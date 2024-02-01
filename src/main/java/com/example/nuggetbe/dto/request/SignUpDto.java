package com.example.nuggetbe.dto.request;

import com.example.nuggetbe.entity.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.antlr.v4.runtime.misc.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class SignUpDto {

    @JsonProperty
    @NotNull
    private Long id;

    @JsonProperty
    @NotNull
    private Role role;

    @JsonProperty
    @NotNull
    @Size(min =1, max = 50, message = "이름은 1글자 이상 50글자 이하여야 합니다.")
    private String name;

    @JsonProperty
    @NotNull
    private String address;

    @JsonProperty
    @NotNull
    private String phoneNumber;

    @JsonProperty
    @NotNull
    @Size(min = 3 , max = 100, message = "이메일은 3글자 이상 100글자 이하여야 합니다.")
    private String email;
}
