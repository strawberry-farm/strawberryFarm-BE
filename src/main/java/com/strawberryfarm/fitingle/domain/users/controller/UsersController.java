package com.strawberryfarm.fitingle.domain.users.controller;

import com.strawberryfarm.fitingle.domain.users.dto.UsersDto.UsersDetailUpdateRequestDto;
import com.strawberryfarm.fitingle.domain.users.dto.UsersDto.UsersLoginRequestDto;
import com.strawberryfarm.fitingle.domain.users.dto.UsersDto.UsersLoginResponseVo;
import com.strawberryfarm.fitingle.domain.users.dto.UsersDto.UsersPasswordResetRequestDto;
import com.strawberryfarm.fitingle.domain.users.dto.UsersDto.UsersSignUpRequestDto;
import com.strawberryfarm.fitingle.domain.users.dto.emailDto.EmailCertificationConfirmRequestDto;
import com.strawberryfarm.fitingle.domain.users.dto.emailDto.EmailCertificationRequestDto;
import com.strawberryfarm.fitingle.domain.users.service.UsersService;
import com.strawberryfarm.fitingle.domain.users.type.CertificationType;
import com.strawberryfarm.fitingle.dto.ResultDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Users", description = "Users API")
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
    @Operation(summary = "로그인", description = "로그인 api")
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

    @GetMapping("/list")
    public ResponseEntity<?> getUsersList() {
        return ResponseEntity.ok(usersService.getUsersList());
    }
}
