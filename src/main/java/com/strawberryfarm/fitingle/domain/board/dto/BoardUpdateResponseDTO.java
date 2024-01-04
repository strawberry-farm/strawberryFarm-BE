package com.strawberryfarm.fitingle.domain.board.dto;


import com.strawberryfarm.fitingle.domain.BaseEntity;
import com.strawberryfarm.fitingle.dto.BaseDto;
import com.strawberryfarm.fitingle.dto.ResultDto;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoardUpdateResponseDTO extends BaseDto {
    private Long boardsId;
    private String title;
    private LocalDateTime createdDate;
    private LocalDateTime updateDate;

    @Override
    public ResultDto<BoardUpdateResponseDTO> doResultDto(String message, String errorCode) {
        return ResultDto.<BoardUpdateResponseDTO>builder()
                .message(message)
                .data(this)
                .errorCode(errorCode)
                .build();
    }
}
