package com.strawberryfarm.fitingle.domain.apply.dto;

import com.strawberryfarm.fitingle.domain.apply.entity.ApplyStatus;
import com.strawberryfarm.fitingle.dto.BaseDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@RequiredArgsConstructor
@AllArgsConstructor
public class ApplyDto extends BaseDto {
	private Long applyId;
	private List<String> question;
	private List<String> contents;
	private ApplyStatus status;
	private String profileUrl;
	private String nickName;
	private String aboutMe;
	private Long boardId;
	private String boardTitle;
}
