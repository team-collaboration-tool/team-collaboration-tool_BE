package com.hyupmin.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserPasswordUpdateRequest {

    @NotBlank(message = "현재 비밀번호는 필수입니다.")
    private String currentPassword;

    @NotBlank(message = "새 비밀번호는 필수입니다.")
    @Pattern(
        regexp = "^[a-zA-Z0-9!@#$%^&*()\\-_+=]{8,20}$",
        message = "비밀번호는 8~20자의 영문 대소문자, 숫자, 특수문자(!@#$%^&*()-_+=)만 사용 가능합니다."
    )
    private String newPassword;
}