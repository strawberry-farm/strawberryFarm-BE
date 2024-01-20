package com.strawberryfarm.fitingle.domain.board.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.strawberryfarm.fitingle.domain.board.entity.Days;
import com.strawberryfarm.fitingle.domain.board.entity.PostStatus;
import com.strawberryfarm.fitingle.domain.board.entity.Times;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "검색 결과 Dto")
public interface BoardSearchDTO {
    @JsonIgnore
    @Schema(description = "게시물 제목", example = "조기 축구 모집합니다~")
    String getTitle();

    @Schema(description = "위치명", example = "온천시민공원")
    String getLocation();

    @Schema(description = "분야명", example = "축구")
    String getFieldName();

    @Schema(description = "요일대", example = "ANYDAY")
    Days getDays();

    @Schema(description = "시간대", example = "DAWN")
    Times getTimes();

    @Schema(description = "시간대", example = "5")
    Long getHeadCount();

    @Schema(description = "모집상태", example = "Y")
    PostStatus getPostStatus();

    @Schema(description = "관심여부", example = "Y")
    char getWishYn();

}
