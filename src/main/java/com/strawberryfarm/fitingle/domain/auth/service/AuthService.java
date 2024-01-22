package com.strawberryfarm.fitingle.domain.auth.service;

import com.strawberryfarm.fitingle.domain.ErrorCode;
import com.strawberryfarm.fitingle.domain.auth.dto.RefreshTokenResponseVo;
import com.strawberryfarm.fitingle.domain.auth.dto.emailDto.EmailCertificationConfirmRequestDto;
import com.strawberryfarm.fitingle.domain.auth.dto.emailDto.EmailCertificationConfirmResponseDto;
import com.strawberryfarm.fitingle.domain.auth.dto.emailDto.EmailCertificationRequestDto;
import com.strawberryfarm.fitingle.domain.auth.dto.emailDto.EmailCertificationResponseDto;
import com.strawberryfarm.fitingle.domain.users.dto.usersDto.AuthLoginRequestDto;
import com.strawberryfarm.fitingle.domain.auth.dto.AuthAccessTokenRefreshRequestDto;
import com.strawberryfarm.fitingle.domain.auth.dto.AuthLoginResponseDto;
import com.strawberryfarm.fitingle.domain.auth.dto.AuthLoginResponseVo;
import com.strawberryfarm.fitingle.domain.users.dto.usersDto.UsersLogoutResponseDto;
import com.strawberryfarm.fitingle.domain.auth.dto.AuthPasswordResetRequestDto;
import com.strawberryfarm.fitingle.domain.auth.dto.AuthPasswordResetResponseDto;
import com.strawberryfarm.fitingle.domain.auth.dto.AuthSignUpRequestDto;
import com.strawberryfarm.fitingle.domain.auth.dto.AuthSignUpResponseDto;
import com.strawberryfarm.fitingle.domain.users.entity.Users;
import com.strawberryfarm.fitingle.domain.users.repository.UsersRepository;
import com.strawberryfarm.fitingle.domain.users.status.SignUpType;
import com.strawberryfarm.fitingle.domain.users.status.UsersStatus;
import com.strawberryfarm.fitingle.domain.users.type.CertificationType;
import com.strawberryfarm.fitingle.dto.ResultDto;
import com.strawberryfarm.fitingle.security.JwtTokenManager;
import com.strawberryfarm.fitingle.utils.RandCodeMaker;
import com.strawberryfarm.fitingle.utils.UsersUtil;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
	private final PasswordEncoder passwordEncoder;
	private final UsersRepository usersRepository;
	private final JwtTokenManager jwtTokenManager;
	private final AuthenticationManagerBuilder authenticationManagerBuilder;
	private final String CERTIFICATION_KEY_PREFIX = "certification:";
	private final String PASSWORD_RESET_KEY_PREFIX = "password:";
	private final RedisTemplate redisTemplate;
	private final JavaMailSender mailSender;

	@Value("${spring.mail.username}")
	private String mailSenderEmail;

	@Transactional
	public ResultDto<?> signUp(AuthSignUpRequestDto authSignUpRequestDto) {
		if (!UsersUtil.checkEmailValid(authSignUpRequestDto.getEmail())) {
			return ResultDto.builder()
				.message(ErrorCode.WRONG_EMAIL_FORMAT.getMessage())
				.data(null)
				.errorCode(ErrorCode.WRONG_EMAIL_FORMAT.getCode())
				.build();
		}

		if (!UsersUtil.checkPasswordValid(authSignUpRequestDto.getPassword())) {
			return ResultDto.builder()
				.message(ErrorCode.WRONG_PASSWORD_FORMAT.getMessage())
				.data(null)
				.errorCode(ErrorCode.WRONG_PASSWORD_FORMAT.getCode())
				.build();
		}

		Users newUsers = Users.builder()
			.email(authSignUpRequestDto.getEmail())
			.password(passwordEncoder.encode(authSignUpRequestDto.getPassword()))
			.nickname(authSignUpRequestDto.getNickName())
			.roles("ROLE_USERS")
			.profileImageUrl("default")
			.signUpType(SignUpType.FITINGLE)
			.status(UsersStatus.AUTHORIZED)
			.createdDate(LocalDateTime.now())
			.updateDate(LocalDateTime.now())
			.build();

		Users save = usersRepository.save(newUsers);

		return AuthSignUpResponseDto.builder()
			.email(newUsers.getEmail())
			.nickName(newUsers.getNickname())
			.createdDate(newUsers.getCreatedDate())
			.updateDate(newUsers.getUpdateDate())
			.build()
			.doResultDto(ErrorCode.SUCCESS.getMessage(), ErrorCode.SUCCESS.getCode());
	}

	@Transactional
	public ResultDto<?> login(AuthLoginRequestDto authLoginRequestDto, Long userId) {
		String email = authLoginRequestDto.getEmail();
		String password = authLoginRequestDto.getPassword();

		if (!UsersUtil.checkEmailValid(email)) {
			return ResultDto.builder()
				.message(ErrorCode.WRONG_EMAIL_FORMAT.getMessage())
				.data(null)
				.errorCode(ErrorCode.WRONG_EMAIL_FORMAT.getCode())
				.build();
		}

		try {
			Users findUsers = usersRepository.findUsersByEmail(email).get();

			UsernamePasswordAuthenticationToken authenticationToken =
				new UsernamePasswordAuthenticationToken(Long.toString(userId), password);

			Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);


			String nickname = findUsers.getNickname();

			//실제 서비스에서는 아래 주석으로 교체
			//String accessToken = jwtTokenManager.genAccessToken(authentication, findUsers.getId());
			String accessToken = jwtTokenManager.genAccessTokenWithExpiredTime(authentication,
				findUsers.getId(), authLoginRequestDto.getExpiredTime());

			System.out.println("authentication.getName() : " + authentication.getName());
			String refreshToken = jwtTokenManager.genRefreshToken(email);

			refreshRefreshToken(email, refreshToken);

			return AuthLoginResponseVo.builder()
				.authLoginResponseDto(AuthLoginResponseDto.builder()
					.email(email)
					.nickName(nickname)
					.accessToken(accessToken)
					.build())
				.refreshToken(refreshToken)
				.build()
				.doResultDto(ErrorCode.SUCCESS.getMessage(), ErrorCode.SUCCESS.getCode());
		} catch (Exception e) {
			return ResultDto.builder()
				.message(ErrorCode.INCORRECT_AUTH_INFO.getMessage())
				.data(null)
				.errorCode(ErrorCode.INCORRECT_AUTH_INFO.getCode())
				.build();
		}
	}

	private void refreshRefreshToken(String email, String refreshToken) {
		redisTemplate.opsForValue().set(email
			, refreshToken
			,jwtTokenManager.getRefreshTokenExpiredTime(),TimeUnit.MILLISECONDS);
	}

	@Transactional
	public ResultDto<?> refreshAccessToken(Long userId, AuthAccessTokenRefreshRequestDto authAccessTokenRefreshRequestDto) {
		Optional<Users> findUsers = usersRepository.findById(userId);
		Users findUser = findUsers.get();
		String email = findUser.getEmail();

		if (!UsersUtil.checkEmailValid(email)) {
			return ResultDto.builder()
				.message(ErrorCode.WRONG_EMAIL_FORMAT.getMessage())
				.data(null)
				.errorCode(ErrorCode.WRONG_EMAIL_FORMAT.getCode())
				.build();
		}

		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

			//실제 서비스에서는 아래 주석으로 교체
			//String accessToken = jwtTokenManager.genAccessToken(authentication, findUsers.getId());
			String accessToken = jwtTokenManager.genAccessTokenWithExpiredTime(authentication,
				userId, authAccessTokenRefreshRequestDto.getExpiredTime());


			System.out.println("authentication.getName()2 : " + authentication.getName());
			String refreshToken = jwtTokenManager.genRefreshToken(email);

			refreshRefreshToken(email, refreshToken);

			return RefreshTokenResponseVo.builder()
				.accessToken(accessToken)
				.refreshToken(refreshToken)
				.build()
				.doResultDto(ErrorCode.SUCCESS.getMessage(), ErrorCode.SUCCESS.getCode());
		} catch (Exception e) {
			return ResultDto.builder()
				.message(ErrorCode.INCORRECT_AUTH_INFO.getMessage())
				.data(null)
				.errorCode(ErrorCode.INCORRECT_AUTH_INFO.getCode())
				.build();
		}
	}

	@Transactional
	public ResultDto<?> logout(String refreshToken) {
		String email = jwtTokenManager.getSubject(refreshToken);
		redisTemplate.opsForValue().set(email,"logout",jwtTokenManager.getRefreshTokenExpiredTime(),TimeUnit.SECONDS);
		SecurityContextHolder.clearContext();
		return UsersLogoutResponseDto.builder()
			.email(email)
			.build()
			.doResultDto(ErrorCode.SUCCESS.getMessage(),ErrorCode.SUCCESS.getCode());
	}

	@Transactional
	public ResultDto<?> emailCertification(
		EmailCertificationRequestDto emailCertificationRequestDto) {

		String email = emailCertificationRequestDto.getEmail();
		CertificationType type = emailCertificationRequestDto.getType();
		ResultDto<?> resultDto = emailAndUsersInfoValidation(email);

		if (resultDto != null) {
			return resultDto;
		}

		String subject = "[fitingle] 이메일 인증 코드 발송";

		Integer certificationNumber = RandCodeMaker.genCertificationNumber();

		String htmlContents = "<p>fitingle 인증 번호 입니다.<p>"
			+ "<p> 인증 번호 : " + certificationNumber + "<p>";

		sendEmail(email,subject,htmlContents);

		String key = CERTIFICATION_KEY_PREFIX + email;
		if (type == CertificationType.PASSWORD_RESET) {
			key = PASSWORD_RESET_KEY_PREFIX + email;
		}
		redisTemplate.opsForValue().set(key,Integer.toString(certificationNumber),300, TimeUnit.SECONDS);

		return EmailCertificationResponseDto.builder()
			.email(email)
			.build().doResultDto(ErrorCode.SUCCESS.getMessage(), ErrorCode.SUCCESS.getCode());
	}

	private void sendEmail(String email, String subject, String htmlContents) {
		MimeMessage message = mailSender.createMimeMessage();

		try {
			MimeMessageHelper messageHelper = new MimeMessageHelper(message,true,"UTF-8");

			messageHelper.setTo(email);
			messageHelper.setSubject(subject);
			messageHelper.setFrom(mailSenderEmail,"fitingle");
			messageHelper.setText(htmlContents,true);
			mailSender.send(message);
		} catch (MessagingException | UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public ResultDto<?> emailCertificationConfirm(
		EmailCertificationConfirmRequestDto emailCertificationConfirmRequestDto) {
		String email = emailCertificationConfirmRequestDto.getEmail();
		String code = emailCertificationConfirmRequestDto.getCode();
		CertificationType type = emailCertificationConfirmRequestDto.getType();

		if (!UsersUtil.checkEmailValid(email)) {
			return ResultDto.builder()
				.message(ErrorCode.WRONG_EMAIL_FORMAT.getMessage())
				.data(null)
				.errorCode(ErrorCode.WRONG_EMAIL_FORMAT.getCode())
				.build();
		}

		String key = CERTIFICATION_KEY_PREFIX+email;
		if (type == CertificationType.PASSWORD_RESET) {
			key = PASSWORD_RESET_KEY_PREFIX+email;
		}

		String value = redisTemplate.opsForValue().get(key).toString();
		if (!code.equals(value)) {
			return ResultDto.builder()
				.message(ErrorCode.FAIL_CERTIFICATION.getMessage())
				.data(null)
				.errorCode(ErrorCode.FAIL_CERTIFICATION.getCode())
				.build();
		}

		redisTemplate.opsForValue().set(key,value,1,TimeUnit.SECONDS);


		return EmailCertificationConfirmResponseDto.builder()
			.email(email)
			.build().doResultDto(ErrorCode.SUCCESS.getMessage(),ErrorCode.SUCCESS.getCode());
	}

	public ResultDto<?> passwordEdit(AuthPasswordResetRequestDto authPasswordResetRequestDto) {
		String email = authPasswordResetRequestDto.getEmail();
		String password = authPasswordResetRequestDto.getPassword();

		if (!UsersUtil.checkEmailValid(email)) {
			return ResultDto.builder()
				.message(ErrorCode.WRONG_EMAIL_FORMAT.getMessage())
				.data(null)
				.errorCode(ErrorCode.WRONG_EMAIL_FORMAT.getCode())
				.build();
		}

		Optional<Users> findUsers = usersRepository.findUsersByEmail(email);

		if (!findUsers.isPresent()) {
			return ResultDto.builder()
				.message(ErrorCode.NOT_EXIST_USERS.getMessage())
				.data(null)
				.errorCode(ErrorCode.NOT_EXIST_USERS.getCode())
				.build();
		}

		Users findUser = findUsers.get();
		findUser.modifyPassword(passwordEncoder.encode(password));

		Users updatedUser = usersRepository.save(findUser);

		return AuthPasswordResetResponseDto.builder()
			.email(updatedUser.getEmail())
			.build()
			.doResultDto(ErrorCode.SUCCESS.getMessage(), ErrorCode.SUCCESS.getCode());
	}


	private ResultDto<?> emailAndUsersInfoValidation(String email) {
		if (!UsersUtil.checkEmailValid(email)) {
			return ResultDto.builder()
				.message(ErrorCode.WRONG_EMAIL_FORMAT.getMessage())
				.data(null)
				.errorCode(ErrorCode.WRONG_EMAIL_FORMAT.getCode())
				.build();
		}

		if (usersRepository.findUsersByEmail(email).isPresent()) {
			return ResultDto.builder()
				.message(ErrorCode.ALREADY_EXIST_USERS.getMessage())
				.data(null)
				.errorCode(ErrorCode.ALREADY_EXIST_USERS.getCode())
				.build();
		}

		return null;
	}

	public void DummyInputRedisCertification(String email,Integer code,long ttl) {
		String temp = Integer.toString(code);
		redisTemplate.opsForValue().set(CERTIFICATION_KEY_PREFIX+email,Integer.toString(code),ttl,
			TimeUnit.SECONDS);
	}
	public String getData(String email) {
		return redisTemplate.opsForValue().get(CERTIFICATION_KEY_PREFIX+email).toString();
	}
}
