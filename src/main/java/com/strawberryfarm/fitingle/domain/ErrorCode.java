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
    INCORRECT_AUTH_INFO(200,"US006","Incorrect Password Or Email"),
    WRONG_PASSWORD_FORMAT(200,"US007","Wrong Password Format"),
    LOGOUT_USERS(200,"US008","Already Logout User"),
    DUPLICATE_LOGIN(200,"US009","Already Login Other Device"),
    INTEREST_AREA_NOT_REGISTER(200,"US010","Interest Area Is Not Register"),

    // AUTH
    INVALID_TOKEN_TYPE(200,"AU000","Invalid Access Token Type"),
    EMPTY_ACCESS_TOKEN(200,"AU001","Access Token Empty"),
    INVALID_ACCESS_TOKEN(200,"AU002","Invalid Access Token"),
    EXPIRED_ACCESS_TOKEN(200,"AU003","Expired Access Token"),
    UNSUPPORTED_ACCESS_TOKEN(200,"AU004","Unsupported Access Token"),
    EMPTY_CLAIM_ACCESS_TOKEN(200,"AU005","Empty Claim Access Token"),


    EMPTY_REFRESH_TOKEN(200,"AU001","Refresh Token Empty"),
    INVALID_REFRESH_TOKEN(200,"AU002","Invalid Refresh Token"),
    EXPIRED_REFRESH_TOKEN(200,"AU003","Expired Refresh Token"),
    UNSUPPORTED_REFRESH_TOKEN(200,"AU004","Unsupported Refresh Token"),
    EMPTY_CLAIM_REFRESH_TOKEN(200,"AU005","Empty Claim Refresh Token"),

    //Chat
    MESSAGE_SEND_ERROR(401,"CH000","Web Chat Message Send Error"),

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
