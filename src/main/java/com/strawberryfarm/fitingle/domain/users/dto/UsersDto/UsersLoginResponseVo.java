package com.strawberryfarm.fitingle.domain.users.dto.UsersDto;

import jdk.jfr.Name;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsersLoginResponseVo {
    private UsersLoginResponseDto usersLoginResponseDto;
    private String refreshToken;
}
