package com.project.jvc3.service;

import com.project.jvc3.domain.dto.user.LoginRequest;
import com.project.jvc3.domain.dto.user.LoginResponse;
import com.project.jvc3.domain.dto.user.SignupRequest;
import com.project.jvc3.domain.entity.User;
import com.project.jvc3.domain.types.TokenType;
import com.project.jvc3.exception.user.UserErrorCode;
import com.project.jvc3.exception.user.UserException;
import com.project.jvc3.repository.UserRepository;
import com.project.jvc3.security.jwt.JwtUtils;
import com.project.jvc3.security.key.KeyStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.PrivateKey;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    //회원가입
    public User registerUser(SignupRequest signupRequest) {
        //이메일 형식 확인
        //나중에는 프론트에서 처리
        if(!EmailValidator.isValidEmail(signupRequest.getEmail())){
            throw new UserException(UserErrorCode.INVALID_EMAIL_FORMAT);
        }

        // 이메일 중복 확인
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new UserException(UserErrorCode.EMAIL_ALREADY_EXISTS);
        }

        //닉네임 중복 확인
        if(userRepository.existsByNickname(signupRequest.getNickname())) {
            throw new UserException(UserErrorCode.NICKNAME_ALREADY_EXISTS);
        }

        //패스워드 암호화
        String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());

        //유저 생성
        User user = User.save(signupRequest,encodedPassword);

        //유저 저장 및 리턴
        return userRepository.save(user);
    }

    public LoginResponse loginUser(LoginRequest loginRequest) {
        //user검사
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        //password 검사
        if(!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new UserException(UserErrorCode.INVALID_CURRENT_PASSWORD);
        }

        //email인증 검사
        if(!user.getEmailVerified()) {
            throw new UserException(UserErrorCode.INVALID_VERIFICATION_EMAIL);
        }

        //비공개키 생성
        PrivateKey privateKey = KeyStore.getInstance().getKeyPair().getPrivate();

        //액세스 토큰 생성
        String accessToken = JwtUtils.createToken(user, privateKey, TokenType.ACCESS);

        //리프레시 토큰 생성
        String refreshToken = JwtUtils.createToken(user,privateKey, TokenType.REFRESH);

        LoginResponse loginResponse = new LoginResponse(accessToken,refreshToken);

        return loginResponse;
    }
}
