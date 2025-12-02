package com.hyupmin.dto.project;

import lombok.Builder;
import lombok.Getter;
import com.hyupmin.domain.project.Project;
import com.hyupmin.domain.projectUser.ProjectUser;
import com.hyupmin.domain.user.User;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class ProjectResponseDTO {
    private Long projectPk;
    private String projectName;
    private String ownerName; // 프로젝트 소유자 이름
    private List<ProjectMemberResponse> members; // 프로젝트 멤버 목록
    private String myRole; // 현재 접속자의 프로젝트 역할
    private String myStatus; //     ;; 상태
    private String joinCode;

    public static ProjectResponseDTO from(Project project) {
        // ProjectUser 목록을 ProjectMemberResponse 목록으로 변환
        List<ProjectMemberResponse> members = project.getProjectUsers().stream()
                .map(ProjectMemberResponse::from)
                .collect(Collectors.toList());

        return ProjectResponseDTO.builder()
                .projectPk(project.getProjectPk())
                .projectName(project.getName())
                .ownerName(project.getOwner().getName())
                .members(members)
                .joinCode(project.getJoinCode())
                .build();
    }

    // 권한 확인.
    public static ProjectResponseDTO from(Project project, ProjectUser currentUserProjectUser) {
        // ProjectUser 목록을 ProjectMemberResponse 목록으로 변환
        List<ProjectMemberResponse> members = project.getProjectUsers().stream()
                .map(ProjectMemberResponse::from)
                .collect(Collectors.toList());

        return ProjectResponseDTO.builder()
                .projectPk(project.getProjectPk())
                .projectName(project.getName())
                .ownerName(project.getOwner().getName())
                .members(members)
                .myRole(currentUserProjectUser.getRole().name())
                .myStatus(currentUserProjectUser.getStatus().name())
                .joinCode(project.getJoinCode())
                .build();
    }
}