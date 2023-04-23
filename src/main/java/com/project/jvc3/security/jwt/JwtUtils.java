package com.project.jvc3.security.jwt;

import com.project.jvc3.domain.entity.User;
import com.project.jvc3.domain.types.TokenType;
import com.project.jvc3.domain.types.UserRole;
import com.project.jvc3.exception.jwt.JwtErrorCode;
import com.project.jvc3.exception.jwt.JwtException;
import com.project.jvc3.exception.user.UserErrorCode;
import com.project.jvc3.exception.user.UserException;
import io.jsonwebtoken.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;


@Configuration
@Slf4j
@RequiredArgsConstructor
public class JwtUtils {

    private final PublicKey publicKey;
    private final SigningKeyResolver signingKeyResolver;


    //토큰 생성
    public static String createToken(User user, PrivateKey privateKey, TokenType tokenType) {
        Claims claims = Jwts.claims();
        claims.put("nickname",user.getNickname());
        claims.put("role",user.getUserRole());

        //토큰 타입에 따른 만료시간
        long expirationTimeMs;
        if(tokenType == TokenType.ACCESS) {
            expirationTimeMs = 15*60*1000; //15분
        } else {
            expirationTimeMs = 7*24*60*60*1000; //7일
        }

        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationTimeMs);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(privateKey,SignatureAlgorithm.RS256)
                .compact();
    }

    //쿠키에서 토큰 꺼내기
    public String resolveRefreshTokenFromCookie(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookieName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    //토큰 검증
    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKeyResolver(signingKeyResolver)
                    .build()
                    .parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            throw new JwtException(JwtErrorCode.INVALID_JSON_WEB_TOKEN_ERROR);
        }
    }

    public boolean isTokenExpiringSoon(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKeyResolver(signingKeyResolver)
                    .build()
                    .parseClaimsJws(token);
            LocalDateTime expirationDate = claims.getBody().getExpiration().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            LocalDateTime oneDayAgo = expirationDate.minusDays(1);

            return oneDayAgo.isAfter(LocalDateTime.now());

        } catch (Exception e) {
            throw new JwtException(JwtErrorCode.INVALID_JSON_WEB_TOKEN_ERROR);
        }
    }

    //검증을 위한 keyResolver생성
    private SigningKeyResolver createSigningKeyResolver() {
        return new SigningKeyResolverAdapter() {
            @Override
            public Key resolveSigningKey(JwsHeader header, Claims claims) {
                return publicKey;
            }
        };
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKeyResolver(signingKeyResolver)
                .build()
                .parseClaimsJws(token)
                .getBody();

        String nickname = claims.get("nickname", String.class);
        String roleString = claims.get("role", String.class);
        UserRole role = UserRole.valueOf(roleString);

        log.info("token nickname " + nickname);

        User user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(role.toString());

        return new UsernamePasswordAuthenticationToken(user, "", Collections.singletonList(simpleGrantedAuthority));
    }
}
