package com.project.jvc3.exception;

import com.project.jvc3.domain.dto.Response;
import com.project.jvc3.exception.jwt.JwtException;
import com.project.jvc3.exception.user.UserException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionManager {

    //user
    @ExceptionHandler(UserException.class)
    public ResponseEntity<Response> userExceptionHandler(UserException ue) {
        Response<String> response = Response.failed(ue.getErrorCode().getResultCode(), ue.getErrorCode().getErrorCode());
        return new ResponseEntity<>(response,ue.getErrorCode().getHttpStatus());
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<Response> jwtExceptionHandler(JwtException jwte) {
        Response<String> response = Response.failed(jwte.getErrorCode().getResultCode(), jwte.getErrorCode().getErrorCode());
        return new ResponseEntity<>(response,jwte.getErrorCode().getHttpStatus());
    }
}
