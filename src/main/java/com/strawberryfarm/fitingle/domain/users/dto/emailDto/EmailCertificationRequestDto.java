package com.strawberryfarm.fitingle.domain.users.dto.emailDto;

import com.strawberryfarm.fitingle.domain.users.type.CertificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailCertificationRequestDto {
    @Schema(description = "이메일", type = "String", example = "email1@g.com")
    private String email;
    @Schema(description = "타입", type = "CertificationType", example = "SIGNUP, PASSWORD_RESET")
    private CertificationType type;
}
