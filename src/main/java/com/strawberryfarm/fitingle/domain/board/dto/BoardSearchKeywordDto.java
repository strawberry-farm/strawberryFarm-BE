package com.strawberryfarm.fitingle.domain.board.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.strawberryfarm.fitingle.domain.board.entity.Days;
import com.strawberryfarm.fitingle.domain.board.entity.PostStatus;
import com.strawberryfarm.fitingle.domain.board.entity.Times;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BoardSearchKeywordDto {

    @Schema(description = "게시물 제목", example = "조기 축구 모집합니다~")
    private String title;

    @Schema(description = "위치명", example = "온천시민공원")
    private String location;

    @Schema(description = "분야명", example = "축구")
    private String fieldName;

    @Schema(description = "요일대", example = "ANYDAY")
    private Days days;

    @Schema(description = "시간대", example = "DAWN")
    private Times times;

    @Schema(description = "총인원", example = "5")
    private Long headCount;

    @Schema(description = "모집상태", example = "Y")
    private PostStatus postStatus;

//    @Schema(description = "관심여부", example = "Y")
//    private String wishYn;
}
