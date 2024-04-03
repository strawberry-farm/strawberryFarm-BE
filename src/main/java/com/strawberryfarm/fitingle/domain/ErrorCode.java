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

    //Field
    NOT_EXIST_FIELD(400,"FD000","Not Exist Field"),

    //Boards
    NOT_EXIST_BOARDS(400,"BO000","Not Exist Boards"),
    NOT_OWNER_BOARDS(400,"BO001","Not Owner of Board"),

    //Apply
    NOT_EXIST_APPLY(200,"AP000","Not Exist Apply"),
    NOT_APPLY_YOUR_BOARD(200,"AP001","Can't Apply Your Board"),
    ALREADY_APPLIED(200,"AP002","Already Applied or Waiting"),

    CANNOT_CANCEL_APPROVED_APPLY(200,"AP003","Already Approved Apply Cannot be Cancelled"),

    ALREADY_FULLED(200,"AP004","Can't Apply , Group Is Fulled"),


    //Qna
    NOT_EXIST_QNA(400,"QN000","Not Exist Qna"),
    QNA_PERMISSION_DENIED(403,"QN001","Permission Denied for DeleteQna"),

    //Comment
    COMMENT_PERMISSION_DENIED(403, "CM000", "Permission Denied for Commenting"),
    COMMENT_ALREADY_EXISTS(400, "CM001", "Comment Already Exists"),

    //Wish
    NOT_EXIST_WISH(400,"WI000","Not Exist Wish"),
    WISH_PERMISSION_DENIED(403,"WI001","Permission Denied for DeleteWish"),
    DUPLICATE_WISH(403,"WI002","Wish Already Exists"),

    //Group
    NOT_EXIST_GROUP(400,"GO000","Not Exist Group"),




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
