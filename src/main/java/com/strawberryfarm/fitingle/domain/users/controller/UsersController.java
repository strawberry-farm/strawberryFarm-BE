package com.strawberryfarm.fitingle.domain.users.controller;

import com.strawberryfarm.fitingle.domain.users.dto.UsersDto.UsersLoginRequestDto;
import com.strawberryfarm.fitingle.domain.users.dto.UsersDto.UsersLoginResponseVo;
import com.strawberryfarm.fitingle.domain.users.dto.UsersDto.UsersSignUpRequestDto;
import com.strawberryfarm.fitingle.domain.users.dto.emailDto.EmailCertificationRequestDto;
import com.strawberryfarm.fitingle.domain.users.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(value = "/auth",produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class UsersController {
    private final UsersService usersService;

    @PostMapping("/email-request")
    public ResponseEntity<?> emailCertificationRequest(@RequestBody EmailCertificationRequestDto emailCertificationRequestDto) {
        return ResponseEntity.ok("wait");
    }
    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody UsersSignUpRequestDto usersSignUpRequestDto) {
        return ResponseEntity.ok(usersService.SignUp(usersSignUpRequestDto));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UsersLoginRequestDto usersLoginRequestDto, HttpServletResponse httpServletResponse) {
        UsersLoginResponseVo usersLoginResponseVo = usersService.login(usersLoginRequestDto);

        Cookie cookie = new Cookie("refreshToken",usersLoginResponseVo.getRefreshToken());
        cookie.setHttpOnly(true);
        cookie.setDomain("/");
        httpServletResponse.addCookie(cookie);
        return ResponseEntity.ok(usersLoginResponseVo.getUsersLoginResponseDto());
    }

    @PostMapping("/logout")
    public void logout() {
        usersService.signOut();
    }

    @GetMapping("/list")
    public ResponseEntity<?> getUsersList() {
        return ResponseEntity.ok(usersService.getUsersList());
    }
}
