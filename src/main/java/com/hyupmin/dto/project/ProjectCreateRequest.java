package com.hyupmin.dto.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProjectCreateRequest {
    @NotBlank(message = "프로젝트 제목은 필수입니다.")
    @Size(min = 1, max = 50, message = "프로젝트 이름은 최대 50자입니다.")
    private String projectName;
}