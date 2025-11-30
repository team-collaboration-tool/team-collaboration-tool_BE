package com.hyupmin.controller.user;

import lombok.RequiredArgsConstructor;
import com.hyupmin.domain.user.User;
import com.hyupmin.dto.user.UserPasswordUpdateRequest;
import com.hyupmin.dto.user.UserSignupRequestDTO;
import com.hyupmin.dto.user.UserUpdateRequest;
import com.hyupmin.service.user.UserService;
import com.hyupmin.config.jwt.JwtTokenProvider; // ✅ JWT 유틸 import 추가
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // ✅ 추가
import org.springframework.web.bind.annotation.*;

import java.util.Map; // ✅ Map import 추가

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder; // ✅ 암호화기 주입
    private final JwtTokenProvider jwtTokenProvider;     // ✅ JWT 토큰 유틸 주입

    /**
     * 회원가입 (비밀번호 암호화 + 검증)
     */
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody UserSignupRequestDTO request) {
        User savedUser = userService.registerUser(request);
        return ResponseEntity.ok("회원가입 성공 ✅ (비밀번호 암호화 완료)\nEmail: " + savedUser.getEmail());
    }

    /**
     * 로그인 (JWT 토큰 발급)
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");

        User user = userService.findByEmail(email);
        // 🔹 존재하지 않는 이메일인 경우
        if (user == null) {
            return ResponseEntity.status(404).body("해당 이메일의 사용자가 존재하지 않습니다.");
        }

        // 🔹 비밀번호 불일치
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.status(401).body("비밀번호가 일치하지 않습니다.");
        }

        String token = jwtTokenProvider.generateToken(user.getEmail());
        return ResponseEntity.ok(Map.of("token", token));
    }

    // 🔹 프로필 수정
    @PatchMapping("/update")
    public ResponseEntity<String> updateUser(
            @AuthenticationPrincipal String userEmail, // JWT 필터에서 설정된 인증 정보
            @RequestBody UserUpdateRequest request) {

        userService.updateUser(userEmail, request);
        return ResponseEntity.ok("회원 정보가 성공적으로 수정되었습니다.");
    }
    // 비밀번호 변경
    @PatchMapping("/update/password")
    public ResponseEntity<String> updatePassword(
            @AuthenticationPrincipal String userEmail,
            @RequestBody UserPasswordUpdateRequest request) {
        try {
            userService.updatePassword(userEmail, request);
            return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }
}