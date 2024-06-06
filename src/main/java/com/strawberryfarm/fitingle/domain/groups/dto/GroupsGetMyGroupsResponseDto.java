package com.strawberryfarm.fitingle.domain.groups.dto;

import com.strawberryfarm.fitingle.domain.board.dto.BoardDetailResponseDTO;
import com.strawberryfarm.fitingle.dto.BaseDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@RequiredArgsConstructor
@Getter
public class GroupsGetMyGroupsResponseDto extends BaseDto {
	private List<PostDetailDto> boards;
}
