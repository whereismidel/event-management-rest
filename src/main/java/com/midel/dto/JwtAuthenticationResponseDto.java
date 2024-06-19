package com.midel.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class JwtAuthenticationResponseDto {

    private String token;

    @JsonCreator
    public JwtAuthenticationResponseDto(@JsonProperty("token") String token) {
        this.token = token;
    }
}
