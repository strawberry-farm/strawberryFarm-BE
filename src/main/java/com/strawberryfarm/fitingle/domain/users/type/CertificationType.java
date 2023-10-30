package com.strawberryfarm.fitingle.domain.users.type;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum CertificationType {
	SIGNUP,PASSWORD_RESET;
	@JsonCreator
	public static CertificationType from(String s) {
		return CertificationType.valueOf(s.toUpperCase());
	}
}
