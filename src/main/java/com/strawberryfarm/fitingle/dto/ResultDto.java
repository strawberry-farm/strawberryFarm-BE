package com.strawberryfarm.fitingle.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.parameters.P;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResultDto<T> {
	private String message;
	private T data;
	private String errorCode;

	public void setResultData(String message, T data, String errorCode) {
		this.message = message;
		this.data = data;
		this.errorCode = errorCode;
	}
}
