package com.strawberryfarm.fitingle.domain.users.service;

import com.strawberryfarm.fitingle.domain.ErrorCode;
import com.strawberryfarm.fitingle.domain.users.dto.UsersDto.UsersAllUsersResponse;
import com.strawberryfarm.fitingle.domain.users.dto.UsersDto.UsersDetailUpdateRequestDto;
import com.strawberryfarm.fitingle.domain.users.dto.UsersDto.UsersDetailResponseDto;
import com.strawberryfarm.fitingle.domain.users.dto.UsersDto.UsersDetailUpdateResponseDto;
import com.strawberryfarm.fitingle.domain.users.dto.UsersDto.UsersLoginRequestDto;
import com.strawberryfarm.fitingle.domain.users.dto.UsersDto.UsersLoginResponseDto;
import com.strawberryfarm.fitingle.domain.users.dto.UsersDto.UsersLoginResponseVo;
import com.strawberryfarm.fitingle.domain.users.dto.UsersDto.UsersLogoutResponseDto;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsersService {
    private final PasswordEncoder passwordEncoder;
    private final UsersRepository usersRepository;
    private final JwtTokenManager jwtTokenManager;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JavaMailSender mailSender;
    private final RedisTemplate redisTemplate;
    private final String CERTIFICATION_KEY_PREFIX = "certification:";

    @Value("${spring.mail.username}")
    private String mailSenderEmail;

    @Transactional
    public ResultDto<?> emailCertification(
        EmailCertificationRequestDto emailCertificationRequestDto) {
        if (!checkEmailValid(emailCertificationRequestDto.getEmail())) {
            return ResultDto.builder()
                .message(ErrorCode.WRONG_EMAIL_FORMAT.getMessage())
                .data(null)
                .errorCode(ErrorCode.WRONG_EMAIL_FORMAT.getCode())
                .build();
        }

        String email = emailCertificationRequestDto.getEmail();
        if (usersRepository.findUsersByEmail(email).isPresent()) {
            return ResultDto.builder()
                .message(ErrorCode.ALREADY_EXIST_USERS.getMessage())
                .data(null)
                .errorCode(ErrorCode.ALREADY_EXIST_USERS.getCode())
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
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public ResultDto<?> emailCertificationConfirm(EmailCertificationConfirmRequestDto emailCertificationConfirmRequestDto) {
        String email = emailCertificationConfirmRequestDto.getEmail();
        String code = emailCertificationConfirmRequestDto.getCode();

        if (!checkEmailValid(email)) {
            return ResultDto.builder()
                .message(ErrorCode.WRONG_EMAIL_FORMAT.getMessage())
                .data(null)
                .errorCode(ErrorCode.WRONG_EMAIL_FORMAT.getCode())
                .build();
        }

        String key = CERTIFICATION_KEY_PREFIX+email;

        String value = redisTemplate.opsForValue().get(key).toString();
        if (value == null || !code.equals(value)) {
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

    @Transactional
    public ResultDto<?> signUp(UsersSignUpRequestDto usersSignUpRequestDto) {
        if (!checkPasswordValid(usersSignUpRequestDto.getPassword())) {
            return ResultDto.builder()
                .message("Invalid password format")
                .data(null)
                .errorCode("0000")
                .build();
        }
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

    @Transactional
    public ResultDto<?> login(UsersLoginRequestDto usersLoginRequestDto) {

        if (!checkEmailValid(usersLoginRequestDto.getEmail())) {
            return ResultDto.builder()
                .message("Wrong email")
                .data(null)
                .errorCode("0005")
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

            redisTemplate.opsForValue().set(findUsers.getEmail()
                ,refreshToken
                ,jwtTokenManager.getRefreshTokenExpiredTime(),TimeUnit.MILLISECONDS);


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

    @Transactional
    public ResultDto<?> logout(String refreshToken) {
        String email = jwtTokenManager.getSubject(refreshToken);
        redisTemplate.opsForValue().set(email,"logout",jwtTokenManager.getRefreshTokenExpiredTime(),TimeUnit.SECONDS);
        return UsersLogoutResponseDto.builder()
            .email(email)
            .build()
            .doResultDto(ErrorCode.SUCCESS.getMessage(),ErrorCode.SUCCESS.getCode());
    }

    public ResultDto<?> getUsersDetail(Long userId) {
        Optional<Users> findUsers = usersRepository.findById(userId);

        if (!findUsers.isPresent()) {
            return ResultDto.builder()
                .message(ErrorCode.NOT_EXIST_USERS.getMessage())
                .data(null)
                .errorCode(ErrorCode.NOT_EXIST_USERS.getCode())
                .build();
        }

        Users findUser = findUsers.get();
        return UsersDetailResponseDto.builder()
            .email(findUser.getEmail())
            .nickname(findUser.getNickname())
            .profileUrl(findUser.getProfileImageUrl())
            .aboutMe(findUser.getAboutMe())
            .build()
            .doResultDto(ErrorCode.SUCCESS.getMessage(),ErrorCode.SUCCESS.getCode());
    }

    public ResultDto<?> updateUsersDetail(Long userId, UsersDetailUpdateRequestDto usersDetailUpdateRequestDto) {
        Optional<Users> findUsers = usersRepository.findById(userId);

        if (!findUsers.isPresent()) {
            return ResultDto.builder()
                .message(ErrorCode.NOT_EXIST_USERS.getMessage())
                .data(null)
                .errorCode(ErrorCode.NOT_EXIST_USERS.getCode())
                .build();
        }

        Users findUser = findUsers.get();
        findUser.modifyUsersInfo(usersDetailUpdateRequestDto);

        Users updateUser = usersRepository.save(findUser);

        return UsersDetailUpdateResponseDto.builder()
            .userId(updateUser.getId())
            .email(updateUser.getEmail())
            .profileUrl(updateUser.getProfileImageUrl())
            .nickname(updateUser.getNickname())
            .aboutMe(updateUser.getAboutMe())
            .build()
            .doResultDto(ErrorCode.SUCCESS.getMessage(), ErrorCode.SUCCESS.getCode());
    }

    private boolean checkEmailValid(String email) {
        return email.matches("^[a-zA-Z0-9]([-_.]?[0-9A-Za-z])*@[a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$");
    }

    private boolean checkPasswordValid(String password) {
        return password.matches("^[a-z0-9A-Z~!@#$%^&*()_=+,.?]{6,24}$");
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
    public String getData(String email) {
        return redisTemplate.opsForValue().get(CERTIFICATION_KEY_PREFIX+email).toString();
    }

}