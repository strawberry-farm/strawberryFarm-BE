package com.strawberryfarm.fitingle.dto;


import javax.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@AllArgsConstructor
@MappedSuperclass
@SuperBuilder
public abstract class BaseDto {
	public ResultDto doResultDto(String message,String errorCode){
		return ResultDto.builder()
			.message(message)
			.data(this)
			.errorCode(errorCode)
			.build();
	}
}
