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
public class BoardRegisterRequestDTO {

    //private Long userId; // 회원아이디
    private String title; //제목
    private Long fieldId; //분야
    private Long headcount; //인원
    private String location; //상세 장소
    private String latitude; //위도,경도
    private String longitude;
    private String days; // 요일
    private String times; //시간
    private String question; //신청서
    private String city; //도시
    private String district; //시
    private String b_code;
    private List<String> tags; // 태그 리스트
}
