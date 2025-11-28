package com.hyupmin.repository.user;

import com.hyupmin.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    // 아이디 중복체크용
    boolean existsByEmail(String email);
}