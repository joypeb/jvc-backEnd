package com.project.jvc3.repository;

import com.project.jvc3.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    boolean existsByName(String name);

    boolean existsByNickname(String nickname);

    Optional<User> findByEmail(String email);

    Optional<User> findByName(String name);

    Optional<User> findByNickname(String nickname);
}
