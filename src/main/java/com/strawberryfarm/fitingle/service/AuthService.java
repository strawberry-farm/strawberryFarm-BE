package com.strawberryfarm.fitingle.service;

import com.strawberryfarm.fitingle.domain.users.entity.Users;
import com.strawberryfarm.fitingle.domain.users.repository.UserRepository;
import com.strawberryfarm.fitingle.dto.LoginRequest;
import com.strawberryfarm.fitingle.dto.LoginResponse;
import com.strawberryfarm.fitingle.security.JwtTokenManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final JwtTokenManager jwtTokenManager;

    private final PasswordEncoder passwordEncoder;
    public ResponseEntity<?> authenticateUser(LoginRequest loginRequest) {

        log.info("----------서비스 호출 ------------");
        Users user = userRepository.findByEmail(loginRequest.getEmail());
        System.out.println("user = " + user);

        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());

        // 토큰 생성
        String accessToken = jwtTokenManager.createAccessToken(authentication);
        String refreshToken = jwtTokenManager.createRefreshToken(authentication);

        LoginResponse response = new LoginResponse();
        response.setUserId(user.getId());
        response.setEmail(user.getEmail());
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);

        return ResponseEntity.ok(response);
    }
}

