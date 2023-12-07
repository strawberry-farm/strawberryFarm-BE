package com.strawberryfarm.fitingle.domain.users.controller;

import com.strawberryfarm.fitingle.domain.users.dto.emailDto.EmailCertificationConfirmRequestDto;
import com.strawberryfarm.fitingle.domain.users.dto.emailDto.EmailCertificationRequestDto;
import com.strawberryfarm.fitingle.domain.users.dto.interestArea.InterestAreaRegisterRequestDto;
import com.strawberryfarm.fitingle.domain.users.dto.keyword.KeywordRegisterRequestDto;
import com.strawberryfarm.fitingle.domain.users.dto.usersDto.UsersDetailUpdateRequestDto;
import com.strawberryfarm.fitingle.domain.users.dto.usersDto.UsersLoginRequestDto;
import com.strawberryfarm.fitingle.domain.users.dto.usersDto.UsersLoginResponseVo;
import com.strawberryfarm.fitingle.domain.users.dto.usersDto.UsersPasswordResetRequestDto;
import com.strawberryfarm.fitingle.domain.users.dto.usersDto.UsersSignUpRequestDto;
import com.strawberryfarm.fitingle.domain.users.service.UsersService;
import com.strawberryfarm.fitingle.dto.ResultDto;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class UsersController {
    private final UsersService usersService;

    @PostMapping("/auth/email-request")
    public ResponseEntity<?> emailCertificationRequest(@RequestBody EmailCertificationRequestDto emailCertificationRequestDto) {
        return ResponseEntity.ok(usersService.emailCertification(emailCertificationRequestDto));
    }

    @PostMapping("/auth/email-confirm")
    public ResponseEntity<?> emailCertificationConfirm(@RequestBody
        EmailCertificationConfirmRequestDto emailCertificationConfirmRequestDto) {
        return ResponseEntity.ok(usersService.emailCertificationConfirm(emailCertificationConfirmRequestDto));
    }

    @PostMapping("/auth/password-edit")
    public ResponseEntity<?> passwordEdit(@RequestBody UsersPasswordResetRequestDto usersPasswordResetRequestDto){
        return ResponseEntity.ok(usersService.passwordEdit(usersPasswordResetRequestDto));
    }

    @PostMapping("/auth/signup")
    public ResponseEntity<?> signUp(@RequestBody UsersSignUpRequestDto usersSignUpRequestDto) {
        return ResponseEntity.ok(usersService.signUp(usersSignUpRequestDto));
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody UsersLoginRequestDto usersLoginRequestDto, HttpServletResponse httpServletResponse) {
        ResultDto resultDto = usersService.login(usersLoginRequestDto);

        if (resultDto.getData() == null) {
            return ResponseEntity.ok(ResultDto.builder()
                    .message(resultDto.getMessage())
                    .data(null)
                    .errorCode(resultDto.getErrorCode())
                .build());
        }

        Cookie cookie = new Cookie("refreshToken",((UsersLoginResponseVo)resultDto.getData()).getRefreshToken());
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        httpServletResponse.addCookie(cookie);

        return ResponseEntity.ok(ResultDto.builder()
            .message(resultDto.getMessage())
            .data(((UsersLoginResponseVo)resultDto.getData()).getUsersLoginResponseDto())
            .errorCode(resultDto.getErrorCode())
            .build());
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<?> logout(@CookieValue("refreshToken") String refreshToken) {
        return ResponseEntity.ok(usersService.logout(refreshToken));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUsersDetail(@PathVariable Long userId) {
        return ResponseEntity.ok(usersService.getUsersDetail(userId));
    }

    @PatchMapping("/user/{userId}")
    public ResponseEntity<?> updateUsersDetail(@PathVariable Long userId, @RequestBody
    UsersDetailUpdateRequestDto usersDetailUpdateRequestDto) {
        return ResponseEntity.ok(usersService.updateUsersDetail(userId,usersDetailUpdateRequestDto));
    }

    @GetMapping("/user/interestArea/{userId}")
    public ResponseEntity<?> getInterestArea(@PathVariable Long userId) {
        return ResponseEntity.ok("");
    }

    @PostMapping("/user/interestArea/{userId}")
    public ResponseEntity<?> registerInterestArea(@PathVariable Long userId,@RequestBody InterestAreaRegisterRequestDto interestAreaRegisterRequestDto) {
        return ResponseEntity.ok("");
    }

    @GetMapping("/user/keyword/{userId}")
    public ResponseEntity<?> getKeyword(@PathVariable Long userId) {
        return ResponseEntity.ok("");
    }

    @PostMapping("/user/keyword/{userId}")
    public ResponseEntity<?> registerKeyword(@PathVariable Long userId, @RequestBody KeywordRegisterRequestDto keywordRegisterRequestDto) {
        return ResponseEntity.ok("");
    }

    @DeleteMapping("/user/keyword/{userId}")
    public ResponseEntity<?> deleteKeyword(@PathVariable Long userId,@RequestParam String keyword) {
        return ResponseEntity.ok("");
    }

    @GetMapping("/list")
    public ResponseEntity<?> getUsersList() {
        return ResponseEntity.ok(usersService.getUsersList());
    }
}
