package com.strawberryfarm.fitingle.domain.board.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
@Setter
@AllArgsConstructor
public class QnaDTO {
    private Long userId;
    private Long qnaId;
    private String profile;
    private String nickName;
    private String contents;
    private boolean status;
    private CommentDTO comment;
}
