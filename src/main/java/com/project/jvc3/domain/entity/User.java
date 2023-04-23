package com.project.jvc3.domain.entity;

import com.project.jvc3.domain.dto.user.SignupRequest;
import com.project.jvc3.domain.types.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Builder
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private boolean emailVerified;

    @Column(nullable = false)
    private UserRole userRole;

    @Column(nullable = false)
    private LocalDateTime createdDate;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = true)
    private EmailVerificationToken emailVerificationToken;

    public static User save(SignupRequest signupRequest, String password) {
        return User.builder()
                .email(signupRequest.getEmail())
                .name(signupRequest.getName())
                .nickname(signupRequest.getNickname())
                .password(password)
                .userRole(UserRole.USER)
                .emailVerified(false)
                .createdDate(LocalDateTime.now())
                .build();
    }

    public void verifyEmail() {
        this.emailVerified = true;
    }

    public boolean getEmailVerified() {
        return this.emailVerified;
    }
}
