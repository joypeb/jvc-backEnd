package com.project.jvc3.exception.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class JwtException extends RuntimeException{
    private JwtErrorCode errorCode;
}
