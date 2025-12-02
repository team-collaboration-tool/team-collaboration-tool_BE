package com.hyupmin.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")  // API 경로에 대해
                .allowedOrigins(
                        "http://localhost:3000",  // React 개발 서버
                        "http://localhost:5173",  // Vite 개발 서버
                        "http://3.22.89.177"  // 프론트엔드 EC2 (Elastic IP)
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);  // 1시간 동안 preflight 요청 캐시
    }
}
