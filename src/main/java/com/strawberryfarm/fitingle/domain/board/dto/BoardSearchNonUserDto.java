package com.strawberryfarm.fitingle.domain.board.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.strawberryfarm.fitingle.domain.board.entity.Days;
import com.strawberryfarm.fitingle.domain.board.entity.PostStatus;
import com.strawberryfarm.fitingle.domain.board.entity.Times;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "비회원 검색 결과 Dto")
public class BoardSearchNonUserDto {

    @Schema(description = "게시물 제목", example = "조기 축구 모집합니다~")
    private String title;

    @Schema(description = "위치명", example = "온천시민공원")
    private String location;

    @Schema(description = "분야명", example = "축구")
    private String fieldName;
//    String getFieldName();
//
    @Schema(description = "요일대", example = "ANYDAY")
    private Days days;
//    Days getDays();
//
    @Schema(description = "시간대", example = "DAWN")
    private Times times;
//    Times getTimes();
//
    @Schema(description = "시간대", example = "5")
    private Long headCount;
//    Long getHeadCount();
//
    @Schema(description = "모집상태", example = "Y")
    private PostStatus postStatus;
//    PostStatus getPostStatus();

    @QueryProjection
    public BoardSearchNonUserDto(String title, String location, String fieldName, Days days,
        Times times, Long headCount, PostStatus postStatus) {
        this.title = title;
        this.location = location;
        this.fieldName = fieldName;
        this.days = days;
        this.times = times;
        this.headCount = headCount;
        this.postStatus = postStatus;
    }
}
