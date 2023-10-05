package com.strawberryfarm.fitingle.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResultDto<T> {
	private String message;
	private T data;
	private String errorCode;
}
