package com.project.jvc3.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class EmailVerificationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime expirationDate;

    public static EmailVerificationToken save(User user,String token, LocalDateTime expirationDate) {
        return EmailVerificationToken.builder()
                .user(user)
                .token(token)
                .expirationDate(expirationDate)
                .build();
    }
}