package com.strawberryfarm.fitingle.domain.adminarea.dto;

import com.strawberryfarm.fitingle.dto.BaseDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class AdminAreaResponseDTO extends BaseDto {

    private List<Sido> sido;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Sido {
        private String sidoName;
        private List<Sigungu> sigungu;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Sigungu {
        private String sigunguName;
        private String bCode;
    }
}