package com.hyupmin.dto.project;

import lombok.Builder;
import lombok.Getter;
import com.hyupmin.domain.projectUser.ProjectUser;
import com.hyupmin.domain.projectUser.ProjectUser.ProjectRole; // Role 임포트 추가
import com.hyupmin.domain.projectUser.ProjectUser.ProjectStatus; // Status 임포트 추가

@Getter
@Builder
public class ProjectMemberResponse {
    private Long userPk;
    private String name;
    private String email;
    private ProjectRole role;
    private ProjectStatus status;

    public static ProjectMemberResponse from(ProjectUser projectUser) {
        return ProjectMemberResponse.builder()
                .userPk(projectUser.getUser().getUserPk())
                .name(projectUser.getUser().getName())
                .email(projectUser.getUser().getEmail())
                .role(projectUser.getRole())
                .status(projectUser.getStatus())
                .build();
    }
}