package com.strawberryfarm.fitingle.domain.users.controller;

import com.strawberryfarm.fitingle.domain.users.dto.interestArea.InterestAreaRegisterRequestDto;
import com.strawberryfarm.fitingle.domain.users.dto.keyword.KeywordRegisterRequestDto;
import com.strawberryfarm.fitingle.domain.users.dto.usersDto.UsersDetailUpdateRequestDto;
import com.strawberryfarm.fitingle.domain.users.dto.usersDto.UsersPasswordResetRequestDto;
import com.strawberryfarm.fitingle.domain.users.service.UsersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/user",produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Users", description = "Users API")
public class UsersController {
    private final UsersService usersService;
    @GetMapping
    @Operation(summary = "유저 상세 정보 가져오기")
    public ResponseEntity<?> getUsersDetail(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(usersService.getUsersDetail(userId));
    }

    @PatchMapping
    @Operation(summary = "유저 정보 수정")
    public ResponseEntity<?> updateUsersDetail(@AuthenticationPrincipal UserDetails userDetails, @RequestBody
    UsersDetailUpdateRequestDto usersDetailUpdateRequestDto) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(usersService.updateUsersDetail(userId,usersDetailUpdateRequestDto));
    }

    @GetMapping("/interestArea")
    @Operation(summary = "관심 지역 가져오기")
    public ResponseEntity<?> getInterestArea(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(usersService.getInterestArea(userId));
    }

    @PostMapping("/interestArea")
    @Operation(summary = "관심 지역 등록")
    public ResponseEntity<?> registerInterestArea(@AuthenticationPrincipal UserDetails userDetails,@RequestBody InterestAreaRegisterRequestDto interestAreaRegisterRequestDto) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(usersService.registerInterestArea(userId,interestAreaRegisterRequestDto));
    }

    @GetMapping("/keyword")
    @Operation(summary = "키워드 리스트 가져오기")
    public ResponseEntity<?> getKeyword(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(usersService.getKeyword(userId));
    }

    @PostMapping("/keyword")
    @Operation(summary = "키워드 등록")
    public ResponseEntity<?> registerKeyword(@AuthenticationPrincipal UserDetails userDetails, @RequestBody KeywordRegisterRequestDto keywordRegisterRequestDto) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(usersService.registerKeyword(userId,keywordRegisterRequestDto));
    }

    @DeleteMapping("/keyword")
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