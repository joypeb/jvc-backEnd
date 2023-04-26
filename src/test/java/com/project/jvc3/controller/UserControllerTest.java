package com.project.jvc3.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.jvc3.domain.dto.user.SignupRequest;
import com.project.jvc3.domain.entity.EmailVerificationToken;
import com.project.jvc3.domain.entity.User;
import com.project.jvc3.repository.EmailVerificationTokenRepository;
import com.project.jvc3.repository.UserRepository;
import com.project.jvc3.security.key.KeyGenerator;
import com.project.jvc3.security.key.KeyStore;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailVerificationTokenRepository emailVerificationTokenRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    PlatformTransactionManager platformTransactionManager;
    TransactionStatus transactionStatus;

    @BeforeAll
    public static void beforeAll() throws NoSuchAlgorithmException {
        KeyPair keyPair = KeyGenerator.generateKeyPair();
        KeyStore.initialize(keyPair);
    }

    @BeforeEach
    public void beforeEach() {
        transactionStatus = platformTransactionManager.getTransaction(new DefaultTransactionDefinition());
    }

    @AfterEach
    public void afterEach() {
        platformTransactionManager.rollback(transactionStatus);
    }

    private static final String BASE_URL = "/api/v1/users";

    @Test
    @DisplayName("회원가입 성공")
    public void testSignUpUser_success() throws Exception {
        SignupRequest signupRequest = new SignupRequest("test@test.co","password","name","nickname");

        mockMvc.perform(post(BASE_URL + "/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk());

        User user = userRepository.findByEmail(signupRequest.getEmail()).get();

        assertNotNull(user);
        assertEquals(signupRequest.getName(),user.getName());
    }

    @Test
    @DisplayName("회원가입 실패 - 이메일 포맷 에러")
    public void testSignUpUser_failed_emailFormatError() throws Exception {
        SignupRequest signupRequest = new SignupRequest("test","password","name","nickname");

        mockMvc.perform(post(BASE_URL + "/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("회원가입 실패 - 이메일 중복")
    public void testSignUpUser_failed_emailAlreadyExist() throws Exception {
        SignupRequest signupRequest = new SignupRequest("test@test.co","password","name","nickname");

        mockMvc.perform(post(BASE_URL + "/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk());

        SignupRequest signupRequest2 = new SignupRequest("test@test.co","password","name","nickname2");

        mockMvc.perform(post(BASE_URL + "/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("회원가입 실패 - 닉네임 중복")
    public void testSignUpUser_failed_nicknameAlreadyExist() throws Exception {
        SignupRequest signupRequest = new SignupRequest("test@test.co","password","name","nickname");

        mockMvc.perform(post(BASE_URL + "/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk());

        SignupRequest signupRequest2 = new SignupRequest("test2@test.co","password","name","nickname");

        mockMvc.perform(post(BASE_URL + "/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("이메일 인증 성공")
    public void testEmailVerification_success() throws Exception {
        SignupRequest signupRequest = new SignupRequest("test@test.co","password","name","nickname");

        mockMvc.perform(post(BASE_URL + "/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk());

        EmailVerificationToken emailVerificationToken = emailVerificationTokenRepository.findByUserEmail(signupRequest.getEmail()).get();

        mockMvc.perform(get(BASE_URL + "/verify-email")
                .param("token",emailVerificationToken.getToken()))
                .andExpect(status().isOk());

        User user = userRepository.findByEmail(signupRequest.getEmail()).get();

        assertTrue(user.getEmailVerified());
    }

    @Test
    @DisplayName("이메일 인증 실패 - 토큰 기간 만료")
    public void testEmailVerification_failed_tokenExpired() throws Exception {
        SignupRequest signupRequest = new SignupRequest("test@test.co","password","name","nickname");

        mockMvc.perform(post(BASE_URL + "/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk());

        EmailVerificationToken emailVerificationToken = emailVerificationTokenRepository.findByUserEmail(signupRequest.getEmail()).get();

        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime oneDayBefore = currentTime.minusDays(1);

        EmailVerificationToken oneDayBeforeToken =  new EmailVerificationToken(emailVerificationToken.getId(), emailVerificationToken.getToken(), emailVerificationToken.getUser(), oneDayBefore);
        emailVerificationTokenRepository.save(oneDayBeforeToken);

        mockMvc.perform(get(BASE_URL + "/verify-email")
                        .param("token",emailVerificationToken.getToken()))
                .andExpect(status().isBadRequest());
    }
}