package com.strawberryfarm.fitingle.security.exception;

import com.strawberryfarm.fitingle.domain.ErrorCode;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException{
	private ErrorCode errorCode;
	public CustomException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}
}
