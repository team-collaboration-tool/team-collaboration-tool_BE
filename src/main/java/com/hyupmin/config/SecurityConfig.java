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

    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 비활성화
                .csrf(csrf -> csrf.disable())

                // CORS 설정 (프론트엔드 연동)
                .cors(cors -> cors.configurationSource(request -> {
                    var corsConfiguration = new org.springframework.web.cors.CorsConfiguration();
                    corsConfiguration.setAllowedOrigins(java.util.List.of(
                            "http://localhost:3000",
                            "http://localhost:5173",
                            "http://3.22.89.177"  // 프론트엔드 EC2 (Elastic IP)
                    ));
                    corsConfiguration.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
                    corsConfiguration.setAllowedHeaders(java.util.List.of("*"));
                    corsConfiguration.setAllowCredentials(true);
                    corsConfiguration.setMaxAge(3600L);
                    return corsConfiguration;
                }))

                // H2 콘솔 frame 허용
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))

                // 요청 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/api/users/signup", "/api/users/login", "/api/users/check-email").permitAll()
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/v3/api-docs.yaml"
                        ).permitAll()
                        .requestMatchers("/actuator/health", "/actuator/info").permitAll()  // Health Check
                        .anyRequest().authenticated()
                )

                // 기본 로그인 UI 및 Basic Auth 비활성화
                .formLogin(login -> login.disable())
                .httpBasic(basic -> basic.disable())

                // JWT 필터 추가 (UsernamePasswordAuthenticationFilter 전에 실행)
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}