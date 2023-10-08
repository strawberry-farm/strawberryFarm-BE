package com.strawberryfarm.fitingle.domain.users.dto.UsersDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsersLoginResponseDto {
    private Long userId;
    private String email;
    private String nickName;
    private String accessToken;
}
