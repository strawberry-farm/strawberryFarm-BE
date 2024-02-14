package com.strawberryfarm.fitingle.domain.auth.controller;

import com.strawberryfarm.fitingle.domain.ErrorCode;
import com.strawberryfarm.fitingle.domain.auth.dto.RefreshTokenResponseDto;
import com.strawberryfarm.fitingle.domain.auth.dto.RefreshTokenResponseVo;
import com.strawberryfarm.fitingle.domain.auth.service.AuthService;
import com.strawberryfarm.fitingle.domain.auth.dto.emailDto.EmailCertificationConfirmRequestDto;
import com.strawberryfarm.fitingle.domain.auth.dto.emailDto.EmailCertificationRequestDto;
import com.strawberryfarm.fitingle.domain.users.dto.usersDto.AuthLoginRequestDto;
import com.strawberryfarm.fitingle.domain.auth.dto.AuthAccessTokenRefreshRequestDto;
import com.strawberryfarm.fitingle.domain.auth.dto.AuthLoginResponseVo;
import com.strawberryfarm.fitingle.domain.auth.dto.AuthPasswordResetRequestDto;
import com.strawberryfarm.fitingle.domain.auth.dto.AuthSignUpRequestDto;
import com.strawberryfarm.fitingle.dto.ResultDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.CookieGenerator;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/auth",produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Auth", description = "AUTH API")
public class AuthController {
	private final AuthService authService;

	@GetMapping("/cors")
	public ResponseEntity<?> corsTest() {
		return ResponseEntity.ok("corsTest");
	}

	@PostMapping("/email-request")
	@Operation(summary = "이메일 인증 요청")
	public ResponseEntity<?> emailCertificationRequest(
		HttpServletRequest request, @RequestBody EmailCertificationRequestDto emailCertificationRequestDto) {
		return ResponseEntity.ok(authService.emailCertification(emailCertificationRequestDto));
	}

	@PostMapping("/email-confirm")
	@Operation(summary = "이메일 인증 확인")
	public ResponseEntity<?> emailCertificationConfirm(@RequestBody
	EmailCertificationConfirmRequestDto emailCertificationConfirmRequestDto) {
		return ResponseEntity.ok(authService.emailCertificationConfirm(emailCertificationConfirmRequestDto));
	}

	@PostMapping("/refreshToken")
	@Operation(summary = "엑세스 토큰 갱신")
	public ResponseEntity<?> refreshToken(@AuthenticationPrincipal UserDetails userDetails,
		@RequestBody AuthAccessTokenRefreshRequestDto authAccessTokenRefreshRequestDto,
		HttpServletRequest request,
		HttpServletResponse response) {
		Long userId = Long.parseLong(userDetails.getUsername());

		ResultDto resultDto = authService.refreshAccessToken(userId,
			authAccessTokenRefreshRequestDto);

		if (resultDto.getData() == null) {
			return ResponseEntity.ok(ResultDto.builder()
				.message(resultDto.getMessage())
				.data(null)
				.errorCode(resultDto.getErrorCode())
				.build());
		}

		refreshCookie(request, response, resultDto);

		return ResponseEntity.ok(RefreshTokenResponseDto.builder()
				.accessToken(((RefreshTokenResponseVo)resultDto.getData()).getAccessToken())
			.build().doResultDto(ErrorCode.SUCCESS.getMessage(), ErrorCode.SUCCESS.getCode()));
	}

	private static void refreshCookie(HttpServletRequest request, HttpServletResponse response,
		ResultDto resultDto) {
		CookieGenerator cg = new CookieGenerator();
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("refreshToken")) {
					cg.setCookieName("refreshToken");
					cg.setCookieHttpOnly(true);
					cg.setCookieMaxAge(60*60*24);
					cg.setCookiePath("/");
					cg.addCookie(response,((RefreshTokenResponseVo) resultDto.getData()).getRefreshToken());
					break;
				}
			}
		}
	}

	@PostMapping("/logout")
	@Operation(summary = "로그아웃")
	public ResponseEntity<?> logout(@CookieValue("refreshToken") String refreshToken) {
		return ResponseEntity.ok(authService.logout(refreshToken));
	}

	@PostMapping("/login")
	@Operation(summary = "로그인", description = "로그인 api")
	public ResponseEntity<?> login(@RequestBody AuthLoginRequestDto authLoginRequestDto, HttpServletResponse response) {
		ResultDto resultDto = authService.login(authLoginRequestDto);

		if (resultDto.getData() == null) {
			return ResponseEntity.ok(ResultDto.builder()
				.message(resultDto.getMessage())
				.data(null)
				.errorCode(resultDto.getErrorCode())
				.build());
		}

		initCookie(response, resultDto);

		return ResponseEntity.ok(ResultDto.builder()
			.message(resultDto.getMessage())
			.data(((AuthLoginResponseVo)resultDto.getData()).getAuthLoginResponseDto())
			.errorCode(resultDto.getErrorCode())
			.build());
	}

	@PostMapping("/signup")
	@Operation(summary = "회원 가입", description = "회원 가입 API 해당 API 호출 전 이메일 인증 과정 필요")
	public ResponseEntity<?> signUp(@RequestBody AuthSignUpRequestDto authSignUpRequestDto) {
		return ResponseEntity.ok(authService.signUp(authSignUpRequestDto));
	}

	@PostMapping("/password-edit")
	@Operation(summary = "비밀 번호 수정 요청")
	public ResponseEntity<?> passwordEdit(@RequestBody AuthPasswordResetRequestDto authPasswordResetRequestDto){
		return ResponseEntity.ok(authService.passwordEdit(authPasswordResetRequestDto));
	}

	private static void initCookie(HttpServletResponse response, ResultDto resultDto) {
		CookieGenerator cg = new CookieGenerator();
		cg.setCookieName("refreshToken");
		cg.setCookieHttpOnly(true);
		cg.setCookieMaxAge(60*60*24);
		cg.setCookiePath("/");
		cg.addCookie(response,((AuthLoginResponseVo) resultDto.getData()).getRefreshToken());
	}
}
