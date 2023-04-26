package com.project.jvc3.service;

import com.project.jvc3.domain.dto.user.SignupRequest;
import com.project.jvc3.domain.entity.EmailVerificationToken;
import com.project.jvc3.domain.entity.User;
import com.project.jvc3.exception.user.UserErrorCode;
import com.project.jvc3.exception.user.UserException;
import com.project.jvc3.repository.EmailVerificationTokenRepository;
import com.project.jvc3.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender javaMailSender;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final UserRepository userRepository;

    @Async("taskExecutor")
    public void sendVerificationEmail(User user, String token) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("예수비전교회 이메일 인증");
        mailMessage.setText("회원가입을 마치려면 다음 링크를 통해 인증을 받아주세요: "
                + "http://localhost:8080/api/v1/users/verify-email?token=" + token);
        javaMailSender.send(mailMessage);
    }

    @Transactional
    public void getEmailToken(Long id) {
        //user 받아오기
        User user = userRepository.findById(id).orElseThrow( () -> new UserException(UserErrorCode.USER_NOT_FOUND));

        //토큰, 토큰만료시간 추가
        String token = UUID.randomUUID().toString();
        LocalDateTime expirationDate = LocalDateTime.now().plusHours(24L);

        //토큰 객체 생성
        EmailVerificationToken emailVerificationToken = EmailVerificationToken.save(user,token,expirationDate);

        //토큰 저장
        emailVerificationTokenRepository.save(emailVerificationToken);

        //이메일 전송
        sendVerificationEmail(user, emailVerificationToken.getToken());
    }

    @Transactional
    public void verifyEmail(String token) {
        //토큰을 이용해 객체 확인
        EmailVerificationToken emailVerificationToken = emailVerificationTokenRepository.findByToken(token);

        log.info(emailVerificationToken.getUser().getEmail());

        //객체가 존재하지 않을경우
        if (emailVerificationToken == null) {
            throw new UserException(UserErrorCode.INVALID_VERIFICATION_EMAIL_TOKEN);
        }

        //토큰 기간이 만료되었을경우
        if (emailVerificationToken.getExpirationDate().isBefore(LocalDateTime.now())) {
            throw new UserException(UserErrorCode.EMAIL_VERIFICATION_TOKEN_EXPIRED);
        }

        //유저가 존재하지 않을경우
        User user = userRepository.findById(emailVerificationToken.getUser().getId()).orElseThrow(() -> {
            throw new UserException(UserErrorCode.USER_NOT_FOUND);
        });

        //이메일 인증 성공
        user.verifyEmail();
        userRepository.save(user);
    }
}
