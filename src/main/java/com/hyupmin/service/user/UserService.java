package com.hyupmin.service.user;

import lombok.RequiredArgsConstructor;
import com.hyupmin.domain.user.User;
import com.hyupmin.dto.user.UserSignupRequestDTO;
import com.hyupmin.repository.user.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * 이메일 중복 확인
     */
    public boolean isEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * 회원가입 처리 (비밀번호 검증 + 암호화 + 저장)
     */
    @Transactional
    public User registerUser(UserSignupRequestDTO request) {
        // 이메일 중복 확인
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 Email입니다.");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // User 엔티티 생성
        User user = new User(
                encodedPassword,
                request.getName(),
                request.getEmail(),
                request.getPhone(),
                request.getField()
        );

        // DB 저장
        return userRepository.save(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));
    }
}
