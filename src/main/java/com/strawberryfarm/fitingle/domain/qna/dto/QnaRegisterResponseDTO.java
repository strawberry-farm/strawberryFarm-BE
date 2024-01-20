package com.strawberryfarm.fitingle.domain.qna.dto;

import com.strawberryfarm.fitingle.domain.board.dto.BoardUpdateResponseDTO;
import com.strawberryfarm.fitingle.dto.BaseDto;
import com.strawberryfarm.fitingle.dto.ResultDto;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class QnaRegisterResponseDTO extends BaseDto {

    private Long qnaId;

    @Override
    public ResultDto<QnaRegisterResponseDTO> doResultDto(String message, String errorCode) {

        return ResultDto.<QnaRegisterResponseDTO>builder()
                .message(message)
                .data(this)
                .errorCode(errorCode)
                .build();
    }
}
