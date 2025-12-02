package com.hyupmin.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginRequestDTO {
    @Schema(description = "사용자 이메일", example = "test@gmail.com")
    private String email;

    @Schema(description = "사용자 비밀번호", example = "1234")
    private String password;
}