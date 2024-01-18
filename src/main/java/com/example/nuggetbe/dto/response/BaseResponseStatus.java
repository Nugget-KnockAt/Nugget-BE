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
    INVALID_NUMBER(false, 2002, "잘못된 숫자 형식입니다.");



    private final boolean isSuccess;
    private final int code;
    private final String message;


    BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }

}