package com.project.jvc3.repository;

import com.project.jvc3.domain.entity.EmailVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {
    EmailVerificationToken findByToken(String token);

    Optional<EmailVerificationToken> findByUserEmail(String email);
}
