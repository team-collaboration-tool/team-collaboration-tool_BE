package com.hyupmin.service.user;

import lombok.RequiredArgsConstructor;
import com.hyupmin.domain.user.User;
import com.hyupmin.dto.user.UserPasswordUpdateRequest;
import com.hyupmin.dto.user.UserSignupRequestDTO;
import com.hyupmin.dto.user.UserUpdateRequest;
import com.hyupmin.repository.user.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * 회원가입 처리 (비밀번호 검증 + 암호화 + 저장)
     */
    @Transactional
    public User registerUser(UserSignupRequestDTO request) {
        // ✅ Email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 Email입니다.");
        }
        // ✅ 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        // ✅ User 엔티티 생성 (암호화된 비밀번호로 저장)
        User user = new User(
                encodedPassword,
                request.getName(),
                request.getEmail(),
                request.getPhone(),
                request.getField()
        );
        // ✅ DB 저장
        return userRepository.save(user);
    }
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));
    }

    @Transactional
    public void updateUser(String email, UserUpdateRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 업데이트할 필드만 변경
        if (request.getName() != null) user.setName(request.getName());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getField() != null) user.setField(request.getField());

        userRepository.save(user);
    }

    @Transactional
    public void updatePassword(String email, UserPasswordUpdateRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 🔹 현재 비밀번호 일치 여부 확인
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        } // 이부분 던진거 잡아야함

        // 🔹 새 비밀번호 암호화 후 저장
        String encodedNewPassword = passwordEncoder.encode(request.getNewPassword());
        user.setPassword(encodedNewPassword);
    }

}