package com.hyupmin.dto.project;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProjectCodeResponse {
    private Long projectPk;
    private String projectName;
    private String joinCode;
}