package com.strawberryfarm.fitingle.domain.board.dto;


import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@ToString
public class BoardUpdateRequestDTO {

    private String title; //제목
    private String contents; //내용
    private Long fieldId; //분야
    private Long headcount; //인원
    private String detail; //상세 장소
    private String latitude; //위도,경도
    private String longitude;
    private String days; // 요일
    private String times; //시간
    private List<String> question; //신청서
    private String bcode; // 지역코드 5자리
    private List<String> tags; // 태그 리스트
    private String addr;
}