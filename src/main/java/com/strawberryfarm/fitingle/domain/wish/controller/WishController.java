package com.strawberryfarm.fitingle.domain.wish.controller;


import com.strawberryfarm.fitingle.domain.wish.dto.WishRegisterRequestDTO;
import com.strawberryfarm.fitingle.domain.wish.service.WishService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/boards", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class WishController {

    private final WishService wishService;

    @PostMapping("/wish")
    public ResponseEntity<?> wishRegister(@RequestBody WishRegisterRequestDTO wishRegisterRequestDto,
                                          @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(wishService.wishRegister(wishRegisterRequestDto, userId));
    }

    @DeleteMapping("/wish/{wishId}")
    public ResponseEntity<?> wishDelete(@PathVariable Long wishId,
                                        @AuthenticationPrincipal UserDetails userDetails){
        Long userId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(wishService.wishDelete(wishId,userId));
    }

    //todo 향후 위시리스트 보여줄 프론트 페이지 상의 후 반환 값 수정 필요! -> user 에서 처리하는건가?
    @GetMapping("/wish")
    public ResponseEntity<?> wishGetList(@AuthenticationPrincipal UserDetails userDetails){
        Long userId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(wishService.wishGetList(userId));
    }
}

