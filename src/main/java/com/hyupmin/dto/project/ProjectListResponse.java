package com.hyupmin.dto.project;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import com.hyupmin.domain.project.Project;

@Getter
@Setter
@Builder
public class ProjectListResponse {
    private Long projectPk;
    private String projectName;

    public static ProjectListResponse from(Project project) {
        return ProjectListResponse.builder()
                .projectPk(project.getProjectPk())
                .projectName(project.getName())
                .build();
    }
}
