package com.hyupmin.config;

import lombok.RequiredArgsConstructor;
import com.hyupmin.config.jwt.JwtAuthenticationFilter;
import com.hyupmin.config.jwt.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider; // ✅ 주입

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // ✅ CSRF 완전 비활성화 (POST, PUT, DELETE 전부 허용)
                .csrf(csrf -> csrf.disable())

                // ✅ CORS 완전 비활성화
                .cors(cors -> cors.disable())

                // ✅ H2 콘솔 frame 허용
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))

                // ✅ 요청 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/api/users/signup", "/api/users/login").permitAll()
                        .anyRequest().authenticated() // 💡 설정/수정 등은 토큰 필요하도록 변경
                )

                // ✅ 기본 로그인 UI 및 Basic Auth 비활성화
                .formLogin(login -> login.disable())
                .httpBasic(basic -> basic.disable())

                // ✅ JWT 필터 추가 (UsernamePasswordAuthenticationFilter 전에 실행)
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}