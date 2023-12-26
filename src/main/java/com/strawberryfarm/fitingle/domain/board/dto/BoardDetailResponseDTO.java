package com.strawberryfarm.fitingle.domain.board.dto;

import com.strawberryfarm.fitingle.domain.comment.entity.Comment;
import com.strawberryfarm.fitingle.domain.field.entity.Field;
import com.strawberryfarm.fitingle.domain.groups.entity.Groups;
import com.strawberryfarm.fitingle.domain.image.entity.Image;
import com.strawberryfarm.fitingle.domain.qna.entity.Qna;
import com.strawberryfarm.fitingle.domain.tag.entity.Tag;
import com.strawberryfarm.fitingle.domain.users.entity.Users;
import com.strawberryfarm.fitingle.domain.wish.entity.Wish;
import com.strawberryfarm.fitingle.dto.BaseDto;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BoardDetailResponseDTO extends BaseDto {

    //board 정보
    private Long boardId;
    private String postStatus;
    private String titleContents;
    private Long headCount;
    private String city;
    private String district;
    private String BCode;
    private String location;
    private String latitude;
    private String longitude;
    private String question;
    private String days;
    private String times;

    //=======추가 데이터=======
    //user 정보
    private Long userId;
    private String nickname;

    //qnas & comment 정보
    private List<QnaDTO> qnas;

    //field 정보 (field Id, name 필요)
    private Long fieldId;
    private String fieldName;

    //group 정보(현재인원만)
    private int participantCount;

    //image 정보 (board image url 들)
    private List<String> images;

    // tag 정보 (글자 정보만)
    private List<String> tags; //가져와야함.

    //wish 정보(종아요 했는지)
    private boolean wish;
}
