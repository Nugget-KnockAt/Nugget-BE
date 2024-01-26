package com.example.nuggetbe.dto.response;

import lombok.Getter;

@Getter
public enum BaseResponseStatus {

    /**
     * 1000 : Success
     */
    SUCCESS(true, 1000, "요청에 성공했습니다."),

    /**
     * 2XXX : Common
     */
    BAD_REQUEST(false, 2000, "잘못된 매개변수입니다."),
    INTERNAL_SERVER_ERROR(false, 2001, "서버 내부 오류가 발생했습니다."),
    INVALID_NUMBER(false, 2002, "잘못된 숫자 형식입니다."),

    /**
     * 3XXX : Member
     */

    DUPLICATE_EMAIL(false, 3000, "중복된 이메일입니다."),
    NO_SUCH_EMAIL(false, 3001, "해당 이메일이 존재하지 않습니다."),

    WRONG_PASSWORD(false, 3002, "비밀번호가 틀렸습니다."),
    GET_OAUTH_TOKEN_FAILED(false, 3003, "oAuth 토큰 요청 실패"),
    GET_OAUTH_INFO_FAILED(false, 3004, "oAuth Info 요청 실패"),
    NO_SUCH_MEMBER(false,3005 ,"No Such Member" ), INVALID_ROLE(false, 3005, "사용자 역할 없음"),
    FAILED_TO_SAVE_CUSTOM_TOUCH(false,3006 ,"Failed to save custom touch" ),
    FAILED_TO_GET_CUSTOM_TOUCH(false,3007 ,"Failed to get custom touch" ),;



    private final boolean isSuccess;
    private final int code;
    private final String message;


    BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }

}