package com.strawberryfarm.fitingle.domain.adminarea.dto;

import com.strawberryfarm.fitingle.dto.BaseDto;
import com.strawberryfarm.fitingle.dto.ResultDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;


@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminAreaResponseDTO implements BaseDto {
    private String sidoName;
    private List<Sigungu> sigungu;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Sigungu {
        private String sigunguName;
        private String bCode;
    }

    @Override
    public ResultDto doResultDto(String message, String errorCode) {
        return ResultDto.builder()
                .message(message)
                .data(this)
                .errorCode(errorCode)
                .build();
    }

}