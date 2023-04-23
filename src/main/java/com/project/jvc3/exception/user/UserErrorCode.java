package com.project.jvc3.exception.user;

import org.springframework.http.HttpStatus;

public enum UserErrorCode {
    USER_REGISTRATION_FAILED(1,"USER_REGISTRATION_FAILED", HttpStatus.BAD_REQUEST),
    EMAIL_ALREADY_EXISTS(1,"EMAIL_ALREADY_EXISTS", HttpStatus.CONFLICT),
    NICKNAME_ALREADY_EXISTS(1,"NICKNAME_ALREADY_EXISTS", HttpStatus.CONFLICT),
    USERNAME_ALREADY_EXISTS(1,"USERNAME_ALREADY_EXISTS", HttpStatus.CONFLICT),
    INVALID_EMAIL_FORMAT(1,"INVALID_EMAIL_FORMAT", HttpStatus.BAD_REQUEST),
    WEAK_PASSWORD(1,"WEAK_PASSWORD", HttpStatus.BAD_REQUEST),
    AUTHENTICATION_FAILED(1,"AUTHENTICATION_FAILED", HttpStatus.UNAUTHORIZED),
    INVALID_CREDENTIALS(1,"INVALID_CREDENTIALS", HttpStatus.UNAUTHORIZED),
    ACCOUNT_DISABLED(1,"ACCOUNT_DISABLED", HttpStatus.FORBIDDEN),
    ACCOUNT_NOT_VERIFIED(1,"ACCOUNT_NOT_VERIFIED", HttpStatus.FORBIDDEN),
    USER_UPDATE_FAILED(1,"USER_UPDATE_FAILED", HttpStatus.BAD_REQUEST),
    PASSWORD_UPDATE_FAILED(1,"PASSWORD_UPDATE_FAILED", HttpStatus.BAD_REQUEST),
    INVALID_CURRENT_PASSWORD(1,"INVALID_CURRENT_PASSWORD", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(1,"USER_NOT_FOUND", HttpStatus.NOT_FOUND),
    INVALID_USER_ID(1,"INVALID_USER_ID", HttpStatus.BAD_REQUEST),
    INVALID_VERIFICATION_EMAIL_TOKEN(1,"INVALID_VERIFICATION_EMAIL_TOKEN", HttpStatus.BAD_REQUEST),
    INVALID_VERIFICATION_EMAIL(1,"INVALID_VERIFICATION_EMAIL", HttpStatus.BAD_REQUEST),
    EMAIL_VERIFICATION_TOKEN_EXPIRED(1,"EMAIL_VERIFICATION_TOKEN_EXPIRED", HttpStatus.BAD_REQUEST);

    private final int resultCode;
    private final String errorCode;
    private final HttpStatus httpStatus;

    UserErrorCode(int resultCode, String errorCode, HttpStatus httpStatus) {
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
