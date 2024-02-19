package com.strawberryfarm.fitingle.domain.adminarea.dto;


import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
public class RegionCodesResponseDTO {
    private List<RegionCode> regcodes;
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegionCode {
        private String code;
        private String name;
    }
}
