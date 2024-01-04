package com.strawberryfarm.fitingle.domain.board.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
@Setter
public class CommentDTO {
    private Long userId;
    private String nickname;
    private String profile;
    private String contents;
}
