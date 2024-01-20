package com.strawberryfarm.fitingle.domain.auth.dto;

import com.strawberryfarm.fitingle.domain.auth.dto.AuthLoginResponseDto;
import com.strawberryfarm.fitingle.dto.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class AuthLoginResponseVo extends BaseDto {
    private AuthLoginResponseDto authLoginResponseDto;
    private String refreshToken;
}
