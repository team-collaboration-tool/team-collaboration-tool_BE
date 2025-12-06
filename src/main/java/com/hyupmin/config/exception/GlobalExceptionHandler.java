package com.hyupmin.config.exception; // config.exception 패키지 등에 생성하세요.

import com.hyupmin.dto.ErrorResponse; // 1번에서 만든 DTO 임포트
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException; // 👈 권한 예외
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * @Valid 유효성 검사 실패 시 발생하는 예외 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        
        Map<String, Object> response = new HashMap<>();
        Map<String, String> errors = new HashMap<>();
        
        // 모든 필드 에러를 수집
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        response.put("success", false);
        response.put("errors", errors);
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * 1. IllegalArgumentException 처리 (400 Bad Request)
     * - 잘못된 요청 값, 유효하지 않은 코드, 프로젝트명 불일치 등
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value()) // 400
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase()) // "Bad Request"
                .message(ex.getMessage()) // 👈 Service에서 던진 메시지
                .path(request.getDescription(false).substring(4)) // "uri=/api/projects/..." -> "/api/projects/..."
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * 2. AccessDeniedException 처리 (403 Forbidden)
     * - 방장 권한이 없는 경우
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AccessDeniedException ex, WebRequest request) {

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.FORBIDDEN.value()) // 403
                .error(HttpStatus.FORBIDDEN.getReasonPhrase()) // "Forbidden"
                .message(ex.getMessage()) // 👈 Service에서 던진 메시지
                .path(request.getDescription(false).substring(4))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    /**
     * 3. 기타 RuntimeException 처리 (500 Internal Server Error)
     * - UserService에서 User를 못 찾는 경우 등
     * - (참고: User를 못 찾는 경우를 404로 처리하고 싶다면 별도 커스텀 예외가 필요합니다)
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(
            RuntimeException ex, WebRequest request) {

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value()) // 500
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()) // "Internal Server Error"
                .message(ex.getMessage())
                .path(request.getDescription(false).substring(4))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}