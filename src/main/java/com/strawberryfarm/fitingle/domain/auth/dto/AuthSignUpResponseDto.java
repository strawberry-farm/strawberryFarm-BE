package com.strawberryfarm.fitingle.domain.auth.dto;

import com.strawberryfarm.fitingle.dto.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import lombok.experimental.SuperBuilder;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class AuthSignUpResponseDto extends BaseDto {
    private String email;
    private String nickName;
    private LocalDateTime createdDate;
    private LocalDateTime updateDate;

}
