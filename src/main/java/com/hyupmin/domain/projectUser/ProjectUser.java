package com.hyupmin.domain.projectUser;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.hyupmin.domain.project.Project;
import com.hyupmin.domain.user.User;

@Entity
@Getter
@Table(name = "project_user")
@NoArgsConstructor
public class ProjectUser {

    public enum ProjectStatus {
        PENDING,
        APPROVED,
    }

    public enum ProjectRole {
        OWNER,
        MEMBER
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long projectUserPk;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_pk", nullable = false) // DBML: project_pk
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_pk", nullable = false) // DBML: user_pk
    private User user;

    // DBML: role (default: "MEMBER")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProjectRole role;

    // DBML: status (default: "PENDING")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProjectStatus status;

    public ProjectUser(Project project, User user, ProjectStatus status, ProjectRole role) {
        this.project = project;
        this.user = user;
        this.status = status;
        this.role = role;
    }

    // 승인 처리 (PENDING -> APPROVED)
    public void approve() {
        this.status = ProjectStatus.APPROVED;
        // 승인 시 역할은 그대로 MEMBER로 유지 (방장은 Project 생성 시점에 별도로 설정)
    }

    public void changeRole(ProjectRole newRole) {
        this.role = newRole;
    }
}