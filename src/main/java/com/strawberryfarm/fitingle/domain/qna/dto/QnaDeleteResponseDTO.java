package com.strawberryfarm.fitingle.domain.qna.dto;

import com.strawberryfarm.fitingle.dto.BaseDto;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class QnaDeleteResponseDTO extends BaseDto {
    private Long qnaId;
}
