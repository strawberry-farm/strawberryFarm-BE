package com.strawberryfarm.fitingle.domain.users.service;

import com.strawberryfarm.fitingle.domain.ErrorCode;
import com.strawberryfarm.fitingle.domain.users.dto.UsersDto.UsersAllUsersResponse;
import com.strawberryfarm.fitingle.domain.users.dto.UsersDto.UsersLoginRequestDto;
import com.strawberryfarm.fitingle.domain.users.dto.UsersDto.UsersLoginResponseDto;
import com.strawberryfarm.fitingle.domain.users.dto.UsersDto.UsersLoginResponseVo;
import com.strawberryfarm.fitingle.domain.users.dto.UsersDto.UsersSignUpRequestDto;
import com.strawberryfarm.fitingle.domain.users.dto.UsersDto.UsersSignUpResponseDto;
import com.strawberryfarm.fitingle.domain.users.dto.emailDto.EmailCertificationConfirmRequestDto;
import com.strawberryfarm.fitingle.domain.users.dto.emailDto.EmailCertificationConfirmResponseDto;
import com.strawberryfarm.fitingle.domain.users.dto.emailDto.EmailCertificationRequestDto;
import com.strawberryfarm.fitingle.domain.users.dto.emailDto.EmailCertificationResponseDto;
import com.strawberryfarm.fitingle.domain.users.entity.Users;
import com.strawberryfarm.fitingle.domain.users.repository.UsersRepository;
import com.strawberryfarm.fitingle.domain.users.status.SignUpType;
import com.strawberryfarm.fitingle.domain.users.status.UsersStatus;
import com.strawberryfarm.fitingle.dto.ResultDto;
import com.strawberryfarm.fitingle.security.JwtTokenManager;
import com.strawberryfarm.fitingle.utils.RandCodeMaker;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.List;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsersService {
    private final PasswordEncoder passwordEncoder;
    private final UsersRepository usersRepository;
    private final JwtTokenManager jwtTokenManager;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
//    private final UsersCertificationRepository usersCertificationRepository;
    private final JavaMailSender mailSender;
    private final RedisTemplate redisTemplate;
    private final String CERTIFICATION_KEY_PREFIX = "certification:";

    @Value("${spring.mail.username}")
    private String mailSenderEmail;

    public ResultDto emailCertification(
        EmailCertificationRequestDto emailCertificationRequestDto) {
        if (!checkEmailValid(emailCertificationRequestDto.getEmail())) {
            return ResultDto.builder()
                .message("Wrong email")
                .data(null)
                .errorCode("0000")
                .build();
        }

        String email = emailCertificationRequestDto.getEmail();

        if (usersRepository.findUsersByEmail(email).isPresent()) {
            return ResultDto.builder()
                .message("already exist email")
                .data(null)
                .errorCode("0001")
                .build();
        }

        String subject = "[fitingle] 이메일 인증 번호 이메일 입니다.";

        Integer certificationNumber = RandCodeMaker.genCertificationNumber();

        String htmlContents = "<p>fitingle 인증 번호 입니다.<p>"
            + "<p> 인증 번호 : " + certificationNumber + "<p>";

        sendEmail(email,subject,htmlContents);

        String key = CERTIFICATION_KEY_PREFIX + email;
        redisTemplate.opsForValue().set(key,Integer.toString(certificationNumber),300,TimeUnit.SECONDS);

        return EmailCertificationResponseDto.builder()
            .email(email)
            .build().doResultDto("success","1111");
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
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public ResultDto emailCertificationConfirm(EmailCertificationConfirmRequestDto emailCertificationConfirmRequestDto) {
        String email = emailCertificationConfirmRequestDto.getEmail();
        String code = emailCertificationConfirmRequestDto.getCode();

        if (!checkEmailValid(email)) {
            return ResultDto.builder()
                .message(ErrorCode.INVALID_EMAIL.getMessage())
                .data(null)
                .errorCode(ErrorCode.INVALID_EMAIL.getCode())
                .build();
        }

        String key = CERTIFICATION_KEY_PREFIX+email;

        String value = redisTemplate.opsForValue().get(key).toString();
        if (value == null || !code.equals(value)) {
            return ResultDto.builder()
                .message("Fail certificate")
                .data(null)
                .errorCode("0004")
                .build();
        }


        return EmailCertificationConfirmResponseDto.builder()
            .email(email)
            .build().doResultDto("success","1111");
    }

    public ResultDto SignUp(UsersSignUpRequestDto usersSignUpRequestDto) {
        Users newUsers = Users.builder()
                .email(usersSignUpRequestDto.getEmail())
                .password(passwordEncoder.encode(usersSignUpRequestDto.getPassword()))
                .nickname(usersSignUpRequestDto.getNickName())
                .roles("ROLE_USERS")
                .profileImageUrl("default")
                .signUpType(SignUpType.FITINGLE)
                .status(UsersStatus.AUTHORIZED)
                .createdDate(LocalDateTime.now())
                .updateDate(LocalDateTime.now())
                .build();

        usersRepository.save(newUsers);

        return UsersSignUpResponseDto.builder()
                .email(newUsers.getEmail())
                .nickName(newUsers.getNickname())
                .createdDate(newUsers.getCreatedDate())
                .updateDate(newUsers.getUpdateDate())
                .build()
            .doResultDto("success","1111");
    }

    public ResultDto login(UsersLoginRequestDto usersLoginRequestDto) {

        if (!checkEmailValid(usersLoginRequestDto.getEmail())) {
            return ResultDto.builder()
                .message("Wrong email")
                .data(null)
                .errorCode("0000")
                .build();
        }

        try {
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(usersLoginRequestDto.getEmail()
                            ,usersLoginRequestDto.getPassword());

            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

            String accessToken = jwtTokenManager.genAccessToken(authentication);
            String refreshToken = jwtTokenManager.genRefreshToken(authentication.getName());
            Users findUsers = usersRepository.findUsersByEmail(authentication.getName()).get();

            return UsersLoginResponseVo.builder()
                .usersLoginResponseDto(UsersLoginResponseDto.builder()
                    .userId(findUsers.getId())
                    .email(findUsers.getEmail())
                    .nickName(findUsers.getNickname())
                    .accessToken(accessToken)
                    .build())
                .refreshToken(refreshToken)
                .build().doResultDto("success","1111");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResultDto.builder()
                .message("Invalid user cause: incorrect password or not signup users")
                .data(null)
                .errorCode("0003")
                .build();
        }
    }

    public void signOut() {

    }

    private boolean checkEmailValid(String email) {
        return email.matches("^([a-z0-9_\\.-]+)@([\\da-z\\.-]+)\\.([a-z\\.]{2,6})$");
    }



    // 여기서 부터는 무조건 테스트를 위한 메서드들 실제 서비스에서는 사용 X
    public UsersAllUsersResponse getUsersList() {
        List<Users> all = usersRepository.findAll();

        return UsersAllUsersResponse.builder()
            .users(all)
            .build();
    }

    public void DummyInputRedisCertification(String email,Integer code,long ttl) {
        String temp = Integer.toString(code);
        redisTemplate.opsForValue().set(CERTIFICATION_KEY_PREFIX+email,Integer.toString(code),ttl,
            TimeUnit.SECONDS);
    }
    public Integer getData(String email) {
        return (Integer) redisTemplate.opsForValue().get(CERTIFICATION_KEY_PREFIX+email);
    }
}
