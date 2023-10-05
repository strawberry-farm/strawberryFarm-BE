package com.strawberryfarm.fitingle.domain.users.dto.UsersDto;

import com.strawberryfarm.fitingle.domain.users.status.SignUpType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsersLoginRequestDto {
    private String email;
    private String password;
}
