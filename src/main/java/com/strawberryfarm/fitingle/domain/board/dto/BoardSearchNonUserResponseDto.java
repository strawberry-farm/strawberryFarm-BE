package com.strawberryfarm.fitingle.domain.board.dto;

import com.strawberryfarm.fitingle.dto.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "비회원 검색 결과 응답 Dto")
public class BoardSearchNonUserResponseDto extends BaseDto {


    @Schema(description = "전체 검색 수", example = "100")
    private long totalCount;

    @Schema(description = "게시물 목록")
    private List<BoardSearchNonUserDto> boards;

}
