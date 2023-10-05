package com.strawberryfarm.fitingle.domain.users.dto.emailDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailCertificationRequestDto {
    private String email;
}