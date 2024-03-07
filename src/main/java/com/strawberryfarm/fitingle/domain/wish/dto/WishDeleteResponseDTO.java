package com.strawberryfarm.fitingle.domain.wish.dto;

import com.strawberryfarm.fitingle.dto.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WishDeleteResponseDTO extends BaseDto {
    private Long boardsId;
    private Long wishId;
    private boolean wishState;
}
