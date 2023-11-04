package com.strawberryfarm.fitingle.domain.users.dto.usersDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
