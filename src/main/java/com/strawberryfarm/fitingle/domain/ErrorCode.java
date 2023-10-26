package com.strawberryfarm.fitingle.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@JsonFormat(shape = Shape.OBJECT)
public enum ErrorCode {

    // USERS
    AUTHENTICATION_DENIED(403,"US000","Not Authenticated User Please, Login"),
    WRONG_EMAIL_FORMAT(200,"US002","Wrong Email Format"),
    ALREADY_EXIST_USERS(200,"US003","Already Sign Up users"),
    FAIL_CERTIFICATION(200,"US004","Fail Certification"),
    NOT_EXIST_USERS(200,"US005","Not Exist Users"),

    //SUCCESS
    SUCCESS(200,"1111","Success");
    private int status;
    private String code;
    private String message;

    ErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
