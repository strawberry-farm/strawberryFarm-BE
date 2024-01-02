package com.strawberryfarm.fitingle.domain.chat.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.strawberryfarm.fitingle.domain.users.type.CertificationType;

public enum ChatType {
	JOIN,TALK;

	@JsonCreator
	public static ChatType from(String s) {
		return ChatType.valueOf(s.toUpperCase());
	}
}
