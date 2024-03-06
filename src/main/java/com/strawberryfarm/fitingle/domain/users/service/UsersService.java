package com.strawberryfarm.fitingle.domain.users.service;

import com.strawberryfarm.fitingle.domain.ErrorCode;
import com.strawberryfarm.fitingle.domain.adminarea.entity.AdminArea;
import com.strawberryfarm.fitingle.domain.adminarea.repository.AdminAreaRepository;
import com.strawberryfarm.fitingle.domain.keyword.dto.KeywordDto;
import com.strawberryfarm.fitingle.domain.keyword.entity.Keyword;
import com.strawberryfarm.fitingle.domain.keyword.repository.KeywordRepository;
import com.strawberryfarm.fitingle.domain.users.dto.interestArea.InterestAreaDeleteResponseDto;
import com.strawberryfarm.fitingle.domain.users.dto.interestArea.InterestAreaRegisterRequestDto;
import com.strawberryfarm.fitingle.domain.users.dto.interestArea.InterestAreaRegisterResponseDto;
import com.strawberryfarm.fitingle.domain.users.dto.interestArea.InterestAreaResponseDto;
import com.strawberryfarm.fitingle.domain.users.dto.keyword.KeywordDeleteResponseDto;
import com.strawberryfarm.fitingle.domain.users.dto.keyword.KeywordGetResponseDto;
import com.strawberryfarm.fitingle.domain.users.dto.keyword.KeywordRegisterRequestDto;
import com.strawberryfarm.fitingle.domain.users.dto.keyword.KeywordRegisterResponseDto;
import com.strawberryfarm.fitingle.domain.users.dto.usersDto.UsersAllUsersResponse;
import com.strawberryfarm.fitingle.domain.users.dto.usersDto.UsersDetailResponseDto;
import com.strawberryfarm.fitingle.domain.users.dto.usersDto.UsersDetailUpdateRequestDto;
import com.strawberryfarm.fitingle.domain.users.dto.usersDto.UsersDetailUpdateResponseDto;
import com.strawberryfarm.fitingle.domain.users.entity.Users;
import com.strawberryfarm.fitingle.domain.users.repository.UsersRepository;
import com.strawberryfarm.fitingle.dto.ResultDto;
import com.strawberryfarm.fitingle.utils.UsersUtil;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
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
        if (!UsersUtil.checkEmailValid(email)) {
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

    // 여기서 부터는 무조건 테스트를 위한 메서드들 실제 서비스에서는 사용 X
    public UsersAllUsersResponse getUsersList() {
        List<Users> all = usersRepository.findAll();

        return UsersAllUsersResponse.builder()
            .users(all)
            .build();
    }
}