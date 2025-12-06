package com.hyupmin.dto.user;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserUpdateRequest {
    private String name;

    @Pattern(
        regexp = "^\\d{11}$",
        message = "전화번호는 11자리 숫자만 입력 가능합니다. (예: 01012345678)"
    )
    private String phone;

    private String field;
}