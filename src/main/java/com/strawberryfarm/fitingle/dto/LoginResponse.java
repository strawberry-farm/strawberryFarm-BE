package com.strawberryfarm.fitingle.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private Long userId;
    private String email;
    private String accessToken;
    private String refreshToken;
    private int errorCode;
}
