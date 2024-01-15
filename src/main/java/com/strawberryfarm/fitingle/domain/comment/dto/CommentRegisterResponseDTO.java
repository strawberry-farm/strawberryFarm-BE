package com.strawberryfarm.fitingle.domain.comment.dto;

import com.strawberryfarm.fitingle.dto.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentRegisterResponseDTO extends BaseDto {
    Long qndId;
}
