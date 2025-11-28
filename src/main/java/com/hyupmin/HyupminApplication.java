package com.hyupmin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing  // JPA Auditing 활성화 (BaseTimeEntity의 createdAt, updatedAt 자동 관리)
public class HyupminApplication {
    public static void main(String[] args) {
        SpringApplication.run(HyupminApplication.class, args);
    }
}