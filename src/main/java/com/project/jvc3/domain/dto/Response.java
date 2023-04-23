package com.project.jvc3.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class Response<T> {
    //resultCode / 0:성공 / 1:user예외 / 2:jwt예외 / 3:post예외 / 4:admin예외 / 5:기타에러
    private int resultCode;
    private T result;

    public static <T>Response success( T result) {
        return Response.builder()
                .resultCode(0)
                .result(result)
                .build();
    }

    public static <T>Response failed(int resultCode, T result) {
        return Response.builder()
                .resultCode(resultCode)
                .result(result)
                .build();
    }
}
