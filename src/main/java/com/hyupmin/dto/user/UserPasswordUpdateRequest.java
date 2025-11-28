package com.hyupmin.dto.user;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserPasswordUpdateRequest {
    private String currentPassword;
    private String newPassword;
}