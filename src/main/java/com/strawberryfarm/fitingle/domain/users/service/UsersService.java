package com.strawberryfarm.fitingle.domain.users.service;

import com.strawberryfarm.fitingle.domain.users.dto.UsersDto.*;
import com.strawberryfarm.fitingle.domain.users.entity.Users;
import com.strawberryfarm.fitingle.domain.users.repository.UsersRepository;
import com.strawberryfarm.fitingle.domain.users.status.SignUpType;
import com.strawberryfarm.fitingle.domain.users.status.UsersStatus;
import com.strawberryfarm.fitingle.security.JwtTokenManager;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UsersService {
    private final PasswordEncoder passwordEncoder;
    private final UsersRepository usersRepository;
    private final JwtTokenManager jwtTokenManager;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;


    public UsersSignUpResponseDto SignUp(UsersSignUpRequestDto usersSignUpRequestDto) {
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
                .build();
    }

    public UsersAllUsersResponse getUsersList() {
        List<Users> all = usersRepository.findAll();

        return UsersAllUsersResponse.builder()
                .users(all)
                .build();
    }

    public UsersLoginResponseVo login(UsersLoginRequestDto usersLoginRequestDto) {

        if (!checkEmailValid(usersLoginRequestDto.getEmail())) {
            throw new RuntimeException("Wrong email");
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
                            .email(findUsers.getEmail())
                            .nickName(findUsers.getNickname())
                            .accessToken(accessToken)
                            .build())
                    .refreshToken(refreshToken)
                    .build();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("InvalidUsers");
        }
    }

    public void signOut() {

    }

    private boolean checkEmailValid(String email) {
        return email.matches("^([a-z0-9_\\.-]+)@([\\da-z\\.-]+)\\.([a-z\\.]{2,6})$");
    }
}
