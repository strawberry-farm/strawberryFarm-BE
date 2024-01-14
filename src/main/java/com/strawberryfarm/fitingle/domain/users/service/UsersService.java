package com.strawberryfarm.fitingle.domain.users.service;

import com.strawberryfarm.fitingle.domain.ErrorCode;
import com.strawberryfarm.fitingle.domain.adminarea.entity.AdminArea;
import com.strawberryfarm.fitingle.domain.adminarea.repository.AdminAreaRepository;
import com.strawberryfarm.fitingle.domain.keyword.dto.KeywordDto;
import com.strawberryfarm.fitingle.domain.keyword.entity.Keyword;
import com.strawberryfarm.fitingle.domain.keyword.repository.KeywordRepository;
import com.strawberryfarm.fitingle.domain.users.dto.emailDto.EmailCertificationConfirmRequestDto;
import com.strawberryfarm.fitingle.domain.users.dto.emailDto.EmailCertificationConfirmResponseDto;
import com.strawberryfarm.fitingle.domain.users.dto.emailDto.EmailCertificationRequestDto;
import com.strawberryfarm.fitingle.domain.users.dto.emailDto.EmailCertificationResponseDto;
import com.strawberryfarm.fitingle.domain.users.dto.interestArea.InterestAreaDeleteResponseDto;
import com.strawberryfarm.fitingle.domain.users.dto.interestArea.InterestAreaRegisterRequestDto;
import com.strawberryfarm.fitingle.domain.users.dto.interestArea.InterestAreaRegisterResponseDto;
import com.strawberryfarm.fitingle.domain.users.dto.interestArea.InterestAreaResponseDto;
import com.strawberryfarm.fitingle.domain.users.dto.keyword.KeywordDeleteResponseDto;
import com.strawberryfarm.fitingle.domain.users.dto.keyword.KeywordGetResponseDto;
import com.strawberryfarm.fitingle.domain.users.dto.keyword.KeywordRegisterRequestDto;
import com.strawberryfarm.fitingle.domain.users.dto.keyword.KeywordRegisterResponseDto;
import com.strawberryfarm.fitingle.domain.users.dto.usersDto.UsersAccessTokenRefreshRequestDto;
import com.strawberryfarm.fitingle.domain.users.dto.usersDto.UsersAccessTokenRefreshResponseDto;
import com.strawberryfarm.fitingle.domain.users.dto.usersDto.UsersAllUsersResponse;
import com.strawberryfarm.fitingle.domain.users.dto.usersDto.UsersDetailResponseDto;
import com.strawberryfarm.fitingle.domain.users.dto.usersDto.UsersDetailUpdateRequestDto;
import com.strawberryfarm.fitingle.domain.users.dto.usersDto.UsersDetailUpdateResponseDto;
import com.strawberryfarm.fitingle.domain.users.dto.usersDto.UsersLoginRequestDto;
import com.strawberryfarm.fitingle.domain.users.dto.usersDto.UsersLoginResponseDto;
import com.strawberryfarm.fitingle.domain.users.dto.usersDto.UsersLoginResponseVo;
import com.strawberryfarm.fitingle.domain.users.dto.usersDto.UsersLogoutResponseDto;
import com.strawberryfarm.fitingle.domain.users.dto.usersDto.UsersPasswordResetRequestDto;
import com.strawberryfarm.fitingle.domain.users.dto.usersDto.UsersPasswordResetResponseDto;
import com.strawberryfarm.fitingle.domain.users.dto.usersDto.UsersSignUpRequestDto;
import com.strawberryfarm.fitingle.domain.users.dto.usersDto.UsersSignUpResponseDto;
import com.strawberryfarm.fitingle.domain.users.entity.Users;
import com.strawberryfarm.fitingle.domain.users.repository.UsersRepository;
import com.strawberryfarm.fitingle.domain.users.status.SignUpType;
import com.strawberryfarm.fitingle.domain.users.status.UsersStatus;
import com.strawberryfarm.fitingle.domain.users.type.CertificationType;
import com.strawberryfarm.fitingle.dto.ResultDto;
import com.strawberryfarm.fitingle.security.JwtTokenManager;
import com.strawberryfarm.fitingle.utils.RandCodeMaker;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
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
public class UsersService {
    private final PasswordEncoder passwordEncoder;
    private final UsersRepository usersRepository;
    private final AdminAreaRepository adminAreaRepository;
    private final KeywordRepository keywordRepository;
    private final JwtTokenManager jwtTokenManager;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JavaMailSender mailSender;
    private final RedisTemplate redisTemplate;
    private final String CERTIFICATION_KEY_PREFIX = "certification:";
    private final String PASSWORD_RESET_KEY_PREFIX = "password:";

    @Value("${spring.mail.username}")
    private String mailSenderEmail;

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
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public ResultDto<?> emailCertificationConfirm(EmailCertificationConfirmRequestDto emailCertificationConfirmRequestDto) {
        String email = emailCertificationConfirmRequestDto.getEmail();
        String code = emailCertificationConfirmRequestDto.getCode();
        CertificationType type = emailCertificationConfirmRequestDto.getType();

        if (!checkEmailValid(email)) {
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

    @Transactional
    public ResultDto<?> signUp(UsersSignUpRequestDto usersSignUpRequestDto) {
        if (!checkPasswordValid(usersSignUpRequestDto.getPassword())) {
            return ResultDto.builder()
                .message(ErrorCode.WRONG_PASSWORD_FORMAT.getMessage())
                .data(null)
                .errorCode(ErrorCode.WRONG_PASSWORD_FORMAT.getCode())
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

        Users save = usersRepository.save(newUsers);

        return UsersSignUpResponseDto.builder()
            .email(newUsers.getEmail())
            .nickName(newUsers.getNickname())
            .createdDate(newUsers.getCreatedDate())
            .updateDate(newUsers.getUpdateDate())
            .build()
            .doResultDto(ErrorCode.SUCCESS.getMessage(), ErrorCode.SUCCESS.getCode());
    }

    @Transactional
    public ResultDto<?> login(UsersLoginRequestDto usersLoginRequestDto) {

        if (!checkEmailValid(usersLoginRequestDto.getEmail())) {
            return ResultDto.builder()
                .message(ErrorCode.WRONG_EMAIL_FORMAT.getMessage())
                .data(null)
                .errorCode(ErrorCode.WRONG_EMAIL_FORMAT.getCode())
                .build();
        }

        try {
            UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(usersLoginRequestDto.getEmail()
                    ,usersLoginRequestDto.getPassword());

            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

            Users findUsers = usersRepository.findUsersByEmail(authentication.getName()).get();
            //실제 서비스에서는 아래 주석으로 교체
            //String accessToken = jwtTokenManager.genAccessToken(authentication, findUsers.getId());
            String accessToken = jwtTokenManager.genAccessTokenWithExpiredTime(authentication,
                findUsers.getId(), usersLoginRequestDto.getExpiredTime());

            String refreshToken = jwtTokenManager.genRefreshToken(authentication.getName());

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
    public ResultDto<?> refreshAccessToken(Long userId, UsersAccessTokenRefreshRequestDto usersAccessTokenRefreshRequestDto) {
        Optional<Users> findUsers = usersRepository.findById(userId);
        Users findUser = findUsers.get();
        String email = findUser.getEmail();
        if (!checkEmailValid(email)) {
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
                userId, usersAccessTokenRefreshRequestDto.getExpiredTime());

            return UsersAccessTokenRefreshResponseDto.builder()
                .accessToken(accessToken)
                .email(email)
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

    public ResultDto<?> passwordEdit(UsersPasswordResetRequestDto usersPasswordResetRequestDto) {
        String email = usersPasswordResetRequestDto.getEmail();
        String password = usersPasswordResetRequestDto.getPassword();

        if (!checkEmailValid(email)) {
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

        return UsersPasswordResetResponseDto.builder()
            .email(updatedUser.getEmail())
            .build()
            .doResultDto(ErrorCode.SUCCESS.getMessage(), ErrorCode.SUCCESS.getCode());
    }

    public ResultDto<?> getInterestArea(Long userId) {
        Optional<Users> findUsers = usersRepository.findById(userId);

        if (!findUsers.isPresent()) {
            return ResultDto.builder()
                .message(ErrorCode.NOT_EXIST_USERS.getMessage())
                .data(null)
                .errorCode(ErrorCode.NOT_EXIST_USERS.getCode())
                .build();
        }

        Users findUser = findUsers.get();
        String b_code = findUser.getBCode();
        if (b_code == null) {
            return ResultDto.builder()
                .message(ErrorCode.INTEREST_AREA_NOT_REGISTER.getMessage())
                .data(null)
                .errorCode(ErrorCode.INTEREST_AREA_NOT_REGISTER.getCode())
                .build();
        }

        AdminArea adminArea = adminAreaRepository.findAdminAreaByGunguCode(b_code);

        return InterestAreaResponseDto.builder()
            .sido(adminArea.getName())
            .gungu(adminArea.getName())
            .b_code(b_code)
            .build()
            .doResultDto(ErrorCode.SUCCESS.getMessage(), ErrorCode.SUCCESS.getCode());
    }

    public ResultDto<?> registerInterestArea(Long userId, InterestAreaRegisterRequestDto interestAreaRegisterRequestDto) {
        String b_code = interestAreaRegisterRequestDto.getB_code();

        Optional<Users> findUsers = usersRepository.findById(userId);

        if (!findUsers.isPresent()) {
            return ResultDto.builder()
                .message(ErrorCode.NOT_EXIST_USERS.getMessage())
                .data(null)
                .errorCode(ErrorCode.NOT_EXIST_USERS.getCode())
                .build();
        }

        Users findUser = findUsers.get();
        findUser.modifyBCode(b_code);

        return InterestAreaRegisterResponseDto.builder()
            .email(findUser.getEmail())
            .build()
            .doResultDto(ErrorCode.SUCCESS.getMessage(),ErrorCode.SUCCESS.getCode());
    }

    public ResultDto<?> deleteInterestArea(Long userId) {
        Optional<Users> findUsers = usersRepository.findById(userId);

        if (!findUsers.isPresent()) {
            return ResultDto.builder()
                .message(ErrorCode.NOT_EXIST_USERS.getMessage())
                .data(null)
                .errorCode(ErrorCode.NOT_EXIST_USERS.getCode())
                .build();
        }

        Users findUser = findUsers.get();
        findUser.modifyBCode(null);

        return InterestAreaDeleteResponseDto.builder()
            .email(findUser.getEmail())
            .build()
            .doResultDto(ErrorCode.SUCCESS.getMessage(), ErrorCode.SUCCESS.getCode());
    }

    @Transactional
    public ResultDto<?> getKeyword(Long userId) {
        Optional<Users> findUsers = usersRepository.findById(userId);

        if (!findUsers.isPresent()) {
            return ResultDto.builder()
                .message(ErrorCode.NOT_EXIST_USERS.getMessage())
                .data(null)
                .errorCode(ErrorCode.NOT_EXIST_USERS.getCode())
                .build();
        }

        Users findUser = findUsers.get();
        List<KeywordDto> keywords = new ArrayList<>();

        for (int i = 0; i < findUser.getKeywords().size(); i++) {
            keywords.add(KeywordDto.builder()
                .id(findUser.getKeywords().get(i).getId())
                .name(findUser.getKeywords().get(i).getName())
                .build());
        }

        return KeywordGetResponseDto.builder()
            .keywords(keywords)
            .build()
            .doResultDto(ErrorCode.SUCCESS.getMessage(), ErrorCode.SUCCESS.getCode());
    }

    @Transactional
    public ResultDto<?> registerKeyword(Long userId, KeywordRegisterRequestDto keywordRegisterRequestDto) {
        String keyword = keywordRegisterRequestDto.getKeyword();

        Optional<Users> findUsers = usersRepository.findById(userId);

        if (!findUsers.isPresent()) {
            return ResultDto.builder()
                .message(ErrorCode.NOT_EXIST_USERS.getMessage())
                .data(null)
                .errorCode(ErrorCode.NOT_EXIST_USERS.getCode())
                .build();
        }

        Keyword newKeyword = Keyword.builder()
            .name(keyword)
            .updateDate(LocalDateTime.now())
            .createdDate(LocalDateTime.now())
            .build();
        Users findUser = findUsers.get();
        newKeyword.modifyUsers(findUser);

        keywordRepository.save(newKeyword);

        return KeywordRegisterResponseDto.builder()
            .keywords(findUser.getKeywords().stream().map(x -> x.getName()).collect(Collectors.toList()))
            .build().doResultDto(ErrorCode.SUCCESS.getMessage(), ErrorCode.SUCCESS.getCode());
    }

    @Transactional
    public ResultDto<?> deleteKeyword(Long userId, Long keywordId) {
        Optional<Users> findUsers = usersRepository.findById(userId);

        if (!findUsers.isPresent()) {
            return ResultDto.builder()
                .message(ErrorCode.NOT_EXIST_USERS.getMessage())
                .data(null)
                .errorCode(ErrorCode.NOT_EXIST_USERS.getCode())
                .build();
        }

        keywordRepository.deleteById(keywordId);
        Users findUser = findUsers.get();


        return KeywordDeleteResponseDto.builder()
            .email(findUser.getEmail())
            .build()
            .doResultDto(ErrorCode.SUCCESS.getMessage(), ErrorCode.SUCCESS.getCode());
    }

    private ResultDto<?> emailAndUsersInfoValidation(String email) {
        if (!checkEmailValid(email)) {
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