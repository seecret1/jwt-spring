package com.github.seecret.jwtexample.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RefreshTokenRequest {

    @JsonProperty("refreshToken")
    private String refreshToken;
}
