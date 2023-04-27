package com.project.jvc3.security.jwt;

import com.project.jvc3.domain.entity.User;
import com.project.jvc3.domain.types.TokenType;
import com.project.jvc3.domain.types.UserRole;
import com.project.jvc3.exception.jwt.JwtErrorCode;
import com.project.jvc3.exception.jwt.JwtException;
import com.project.jvc3.security.key.KeyStore;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private static final List<String> EXCLUDE_URLS = Arrays.asList("/api/v1/users", "/swagger-ui", "/v3/api-docs");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        /*log.info("경로 : " + request.getServletPath());
        if(request.getServletPath().contains("/api/v1/users")) {
            filterChain.doFilter(request, response);
            return;
        }*/
        try {
            if(!shouldExclude(request)) {
                //액세스 토큰 꺼내오기
                String accessToken = jwtUtils.resolveRefreshTokenFromCookie(request, "access_token");

                //액세스 토큰 claims가져오기
                Jws<Claims> claims = jwtUtils.getClaims(accessToken);

                //액세스 토큰 체크, 검증
                if (accessToken != null && jwtUtils.validateToken(claims, accessToken)) {

                    //인증 및 spring security 등록
                    Authentication authentication = jwtUtils.getAuthentication(claims, accessToken);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    //액세스 토큰이 존재하지 않을경우
                    //리프레시 토큰 꺼내오기
                    String refreshToken = jwtUtils.resolveRefreshTokenFromCookie(request, "refresh_token");
                    Jws<Claims> refreshClaims = jwtUtils.getClaims(refreshToken);

                    //리프레시 토큰 체크, 검증
                    if (refreshToken != null && jwtUtils.validateToken(claims, refreshToken)) {

                        //인증 및 spring security 등록
                        Authentication authentication = jwtUtils.getAuthentication(claims, refreshToken);
                        SecurityContextHolder.getContext().setAuthentication(authentication);

                        //액세스 토큰 발급
                        User user = new User(authentication.getName(), (UserRole) authentication.getAuthorities().toArray()[0]);

                        KeyStore keyStore = KeyStore.getInstance();
                        PrivateKey privateKey = keyStore.getKeyPair().getPrivate();

                        String newAccessToken = JwtUtils.createToken(user, privateKey, TokenType.ACCESS);
                        Cookie newAccessTokenCookie = createTokenCookie("access_token", newAccessToken, 15 * 60);
                        response.addCookie(newAccessTokenCookie);

                        //리프레시 토큰의 유효기간이 1일 이하라면
                        if (jwtUtils.TokenExpiringSoon(refreshClaims, refreshToken)) {
                            //리프레시 토큰 재발급
                            String newRefreshToken = JwtUtils.createToken(user, privateKey, TokenType.REFRESH);
                            Cookie newRefreshTokenCookie = createTokenCookie("refresh_token", newRefreshToken, 7 * 24 * 60 * 60);
                            response.addCookie(newRefreshTokenCookie);
                        }
                    } else {
                        throw new JwtException(JwtErrorCode.REFRESH_JSON_WEB_TOKEN_EXPIRED);
                    }
                }
            }
            filterChain.doFilter(request, response);
        } catch (JwtException e) {
            throw new JwtException(JwtErrorCode.INVALID_JSON_WEB_TOKEN_ERROR);
        }

    }

    private Cookie createTokenCookie(String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        return cookie;
    }

    private boolean shouldExclude(HttpServletRequest request) {
        return EXCLUDE_URLS.stream().anyMatch(url -> request.getRequestURI().contains(url));
    }
}
