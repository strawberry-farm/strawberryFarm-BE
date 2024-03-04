package com.strawberryfarm.fitingle.domain.board.dto;

import com.strawberryfarm.fitingle.domain.board.entity.Board;
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
@Schema(description = "검색 결과 응답 Dto")
public class BoardSearchResponseDto {

    @Schema(description = "전체 검색 수", example = "100")
    private long totalCount;

    @Schema(description = "게시물 목록")
    private List<BoardSearchKeywordDto> boards;
}
