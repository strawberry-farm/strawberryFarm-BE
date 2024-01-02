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
@RequiredArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Users", description = "Users API")
public class UsersController {
    private final UsersService usersService;

    @PostMapping("/auth/email-request")
    @Operation(summary = "이메일 인증 요청")
    public ResponseEntity<?> emailCertificationRequest(HttpServletRequest request, @RequestBody EmailCertificationRequestDto emailCertificationRequestDto) {
        return ResponseEntity.ok(usersService.emailCertification(emailCertificationRequestDto));
    }

    @PostMapping("/auth/email-confirm")
    @Operation(summary = "이메일 인증 확인")
    public ResponseEntity<?> emailCertificationConfirm(@RequestBody
    EmailCertificationConfirmRequestDto emailCertificationConfirmRequestDto) {
        return ResponseEntity.ok(usersService.emailCertificationConfirm(emailCertificationConfirmRequestDto));
    }

    @PostMapping("/auth/password-edit")
    @Operation(summary = "비밀 번호 수정 요청")
    public ResponseEntity<?> passwordEdit(@RequestBody UsersPasswordResetRequestDto usersPasswordResetRequestDto){
        return ResponseEntity.ok(usersService.passwordEdit(usersPasswordResetRequestDto));
    }

    @PostMapping("/auth/signup")
    @Operation(summary = "회원 가입", description = "회원 가입 API 해당 API 호출 전 이메일 인증 과정 필요")
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
    @Operation(summary = "로그아웃")
    public ResponseEntity<?> logout(@CookieValue("refreshToken") String refreshToken) {
        return ResponseEntity.ok(usersService.logout(refreshToken));
    }

    @GetMapping("/user")
    @Operation(summary = "로그아웃")
    public ResponseEntity<?> getUsersDetail(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(usersService.getUsersDetail(userId));
    }

    @PatchMapping("/user")
    @Operation(summary = "유저 정보 수정")
    public ResponseEntity<?> updateUsersDetail(@AuthenticationPrincipal UserDetails userDetails, @RequestBody
    UsersDetailUpdateRequestDto usersDetailUpdateRequestDto) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(usersService.updateUsersDetail(userId,usersDetailUpdateRequestDto));
    }

    @GetMapping("/user/interestArea")
    @Operation(summary = "관심 지역 가져오기")
    public ResponseEntity<?> getInterestArea(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(usersService.getInterestArea(userId));
    }

    @PostMapping("/user/interestArea")
    @Operation(summary = "관심 지역 등록")
    public ResponseEntity<?> registerInterestArea(@AuthenticationPrincipal UserDetails userDetails,@RequestBody InterestAreaRegisterRequestDto interestAreaRegisterRequestDto) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(usersService.registerInterestArea(userId,interestAreaRegisterRequestDto));
    }

    @GetMapping("/user/keyword")
    @Operation(summary = "키워드 리스트 가져오기")
    public ResponseEntity<?> getKeyword(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(usersService.getKeyword(userId));
    }

    @PostMapping("/user/keyword")
    @Operation(summary = "키워드 등록")
    public ResponseEntity<?> registerKeyword(@AuthenticationPrincipal UserDetails userDetails, @RequestBody KeywordRegisterRequestDto keywordRegisterRequestDto) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(usersService.registerKeyword(userId,keywordRegisterRequestDto));
    }

    @DeleteMapping("/user/keyword")
    @Operation(summary = "키워드 삭제")
    public ResponseEntity<?> deleteKeyword(@AuthenticationPrincipal UserDetails userDetails,@RequestParam Long keywordId) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(usersService.deleteKeyword(userId,keywordId));
    }

    @GetMapping("/list")
    @Operation(summary = "디버깅용 사용 X")
    public ResponseEntity<?> getUsersList() {
        return ResponseEntity.ok(usersService.getUsersList());
    }
}