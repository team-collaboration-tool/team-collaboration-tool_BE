package com.hyupmin.repository.project;

import com.hyupmin.domain.project.Project;
import com.hyupmin.domain.projectUser.ProjectUser;
import com.hyupmin.domain.projectUser.ProjectUser.ProjectStatus; // Enum 임포트
import com.hyupmin.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ProjectUserRepository extends JpaRepository<ProjectUser, Long> {
    // 특정 프로젝트에 특정 사용자가 이미 멤버로 있는지 확인.
    Optional<ProjectUser> findByProjectAndUser(Project project, User user);

    // 특정 프로젝트에 특정 사용자가 멤버로 있는지 확인.
    boolean existsByProjectAndUser(Project project, User user);

    // 상태가 'APPROVED' 프로젝트 목록 조회 <- 대시보드 좌측 참여 프로젝트 목록을 띄울 때 사용.
    List<ProjectUser> findByUserAndStatus(User user, ProjectStatus status);

    // 승인 대기 중인 요청 목록 조회 <- 방장이 승인 할 때 사용
    List<ProjectUser> findByProjectAndStatus(Project project, ProjectStatus status);
}