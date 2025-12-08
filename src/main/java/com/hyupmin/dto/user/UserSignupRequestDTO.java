package com.hyupmin.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserSignupRequestDTO {

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Pattern(
        regexp = "^[a-zA-Z0-9!@#$%^&*()\\-_+=]{8,20}$",
        message = "비밀번호는 8~20자의 영문 대소문자, 숫자, 특수문자(!@#$%^&*()-_+=)만 사용 가능합니다."
    )
    private String password;

    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    @NotBlank(message = "이메일은 필수입니다.")
    @Pattern(
        regexp = "^[a-zA-Z0-9]+@[a-zA-Z0-9]+\\.[a-zA-Z]+$",
        message = "이메일 형식이 올바르지 않습니다. (예: example@domain.com)"
    )
    private String email;

    @NotBlank(message = "전화번호는 필수입니다.")
    @Pattern(
        regexp = "^\\d{11}$",
        message = "전화번호는 11자리 숫자만 입력 가능합니다. (예: 01012345678)"
    )
    private String phone;

    private String field;
}