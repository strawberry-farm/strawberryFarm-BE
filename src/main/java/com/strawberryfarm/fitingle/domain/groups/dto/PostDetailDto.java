package com.strawberryfarm.fitingle.domain.groups.dto;

import com.strawberryfarm.fitingle.domain.board.entity.Days;
import com.strawberryfarm.fitingle.domain.board.entity.Times;
import com.strawberryfarm.fitingle.domain.groups.entity.GroupsStatus;
import com.strawberryfarm.fitingle.dto.BaseDto;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@RequiredArgsConstructor
public class PostDetailDto extends BaseDto {
	private Long boardId;
	private String title;
	private boolean wish;
	private String location;
	private Times times;
	private Days days;
	private Long headCount;
	private int participantCount;
	private GroupsStatus status;
	private String fieldName;
	private List<String> imagesUrl;
}
