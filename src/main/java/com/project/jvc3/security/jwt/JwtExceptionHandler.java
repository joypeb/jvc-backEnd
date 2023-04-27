package com.project.jvc3.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.jvc3.domain.dto.Response;
import com.project.jvc3.exception.ExceptionManager;
import com.project.jvc3.exception.jwt.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtExceptionHandler extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (JwtException e) {
            log.info("에러 발생");
            response.setStatus(e.getErrorCode().getHttpStatus().value());
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");

            Response failedResponse = Response.failed(e.getErrorCode().getResultCode(),e.getErrorCode().getErrorCode());

            response.getWriter().write(objectMapper.writeValueAsString(failedResponse));
        }
    }
}
