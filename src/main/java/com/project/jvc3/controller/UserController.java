package com.project.jvc3.controller;

import com.project.jvc3.domain.dto.Response;
import com.project.jvc3.domain.dto.user.LoginRequest;
import com.project.jvc3.domain.dto.user.LoginResponse;
import com.project.jvc3.domain.dto.user.SignupRequest;
import com.project.jvc3.domain.entity.User;
import com.project.jvc3.service.EmailService;
import com.project.jvc3.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;
    private final EmailService emailService;

    //유저 회원가입
    @PostMapping("/signup")
    public ResponseEntity<Response> registerUser(@RequestBody SignupRequest signupRequest) {
        //회원가입 처리
        User user = userService.registerUser(signupRequest);

        //이메일 인증 전송
        emailService.getEmailToken(user);

        return ResponseEntity.ok().body(Response.success("회원가입에 성공하였습니다 이메일을 인증해주세요"));
    }

    //유저 로그인
    @PostMapping("/login")
    public ResponseEntity<Response> loginUser(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        //로그인 처리
        LoginResponse loginResponse = userService.loginUser(loginRequest);

        //토큰을 http-only 쿠키로 설정
        Cookie accessTokenCookie = createTokenCookie("access_token", loginResponse.getAccessToken(),15*60);
        response.addCookie(accessTokenCookie);

        Cookie refreshTokenCookie = createTokenCookie("refresh_token", loginResponse.getRefreshToken(), 7*24*60*60);
        response.addCookie(refreshTokenCookie);


        return ResponseEntity.ok().body(Response.success("로그인 성공"));
    }

    //이메일 인증
    @GetMapping("/verify-email")
    public ResponseEntity<Response> verifyEmail(@RequestParam String token) {

        log.info("이메일 인증 토큰 : " + token);
        //이메일 인증 로직
        emailService.verifyEmail(token);

        return ResponseEntity.ok().body(Response.success("이메일 인증에 성공하였습니다"));
    }

    private Cookie createTokenCookie(String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        return cookie;
    }
}
