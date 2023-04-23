package com.project.jvc3.exception.jwt;

import org.springframework.http.HttpStatus;

public enum JwtErrorCode {
    JSON_WEB_TOKEN_EXPIRED(2,"JSON_WEB_TOKEN_EXPIRED", HttpStatus.UNAUTHORIZED),
    INVALID_JSON_WEB_TOKEN_ERROR(2,"INVALID_JSON_WEB_TOKEN_ERROR", HttpStatus.UNAUTHORIZED),
    REFRESH_JSON_WEB_TOKEN_EXPIRED(2,"REFRESH_JSON_WEB_TOKEN_EXPIRED", HttpStatus.UNAUTHORIZED);

    private final int resultCode;
    private final String errorCode;
    private final HttpStatus httpStatus;

    JwtErrorCode(int resultCode, String errorCode, HttpStatus httpStatus) {
        this.resultCode = resultCode;
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    public int getResultCode() {
        return resultCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
