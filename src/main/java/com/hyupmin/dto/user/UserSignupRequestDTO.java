package com.hyupmin.dto.user;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserSignupRequestDTO {
    private String password;
    private String name;
    private String email;
    private String phone;
    private String field;
}