package com.strawberryfarm.fitingle.domain.users.dto.UsersDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsersSignUpRequestDto {
    private String email;
    private String password;
    private String nickName;
}
