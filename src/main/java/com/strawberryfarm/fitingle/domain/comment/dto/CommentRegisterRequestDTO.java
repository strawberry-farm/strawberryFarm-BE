package com.strawberryfarm.fitingle.domain.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class CommentRegisterRequestDTO {
    Long qnaId;
    String contents;
}
