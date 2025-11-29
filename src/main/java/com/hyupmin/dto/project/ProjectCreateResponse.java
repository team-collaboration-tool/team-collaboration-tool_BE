package com.hyupmin.dto.project;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectCreateResponse {
    private Long projectPk;
    private String projectName;
    private String joinCode;
}
