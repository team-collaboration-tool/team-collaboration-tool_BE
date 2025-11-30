package com.hyupmin.dto; // DTO 패키지에 생성하세요.

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponse {
    private final int status;       // HTTP 상태 코드 (e.g., 403)
    private final String error;    // HTTP 상태 메시지 (e.g., "Forbidden")
    private final String message;  // Service에서 던진 실제 오류 메시지
    private final String path;     // 오류가 발생한 URL 경로
    // 필요시 timestamp 등 추가 가능
}