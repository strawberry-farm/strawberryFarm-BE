package com.strawberryfarm.fitingle.domain.qna.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
@ToString
public class QnaRegisterRequestDTO {
    private Long boardsId;
    private String contents;
    private boolean status;
    //private String password;
}
