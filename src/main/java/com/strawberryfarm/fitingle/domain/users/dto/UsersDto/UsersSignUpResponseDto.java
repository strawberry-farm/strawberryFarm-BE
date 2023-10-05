package com.strawberryfarm.fitingle.domain.users.dto.UsersDto;

import com.strawberryfarm.fitingle.dto.BaseDto;
import com.strawberryfarm.fitingle.dto.ResultDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsersSignUpResponseDto implements BaseDto {
    private String email;
    private String nickName;
    private LocalDateTime createdDate;
    private LocalDateTime updateDate;


    @Override
    public ResultDto doResultDto(String message, String errorCode) {
        return ResultDto.builder()
            .message(message)
            .data(this)
            .errorCode(errorCode)
            .build();
    }
}
