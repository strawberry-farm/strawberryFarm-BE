package com.strawberryfarm.fitingle.domain.users.dto.UsersDto;

import com.strawberryfarm.fitingle.dto.BaseDto;
import com.strawberryfarm.fitingle.dto.ResultDto;
import jdk.jfr.Name;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsersLoginResponseVo implements BaseDto {
    private UsersLoginResponseDto usersLoginResponseDto;
    private String refreshToken;


    @Override
    public ResultDto doResultDto(String message, String errorCode) {
        return ResultDto.builder()
            .message(message)
            .data(this)
            .errorCode(errorCode)
            .build();
    }
}
