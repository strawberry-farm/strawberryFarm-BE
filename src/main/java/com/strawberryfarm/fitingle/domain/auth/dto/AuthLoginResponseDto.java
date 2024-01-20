package com.strawberryfarm.fitingle.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthLoginResponseDto {
    private String email;
    private String nickName;
    private String accessToken;
}
