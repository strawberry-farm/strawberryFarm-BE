package com.strawberryfarm.fitingle.domain.users.dto.usersDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "사용자 로그인 요청 Dto")
public class AuthLoginRequestDto {

	@Schema(description = "이메일", example = "user@example.com")
	private String email;
	@Schema(description = "비밀번호", example = "1234!@")
	private String password;
	@Schema(description = "만료 시간", example = "분 단위")
	private int expiredTime;
}
