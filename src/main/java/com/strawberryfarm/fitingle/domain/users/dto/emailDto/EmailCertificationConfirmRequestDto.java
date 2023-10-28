package com.strawberryfarm.fitingle.domain.users.dto.emailDto;

import com.strawberryfarm.fitingle.domain.users.type.CertificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmailCertificationConfirmRequestDto {
	private String email;
	private String code;
	private CertificationType type;
}
