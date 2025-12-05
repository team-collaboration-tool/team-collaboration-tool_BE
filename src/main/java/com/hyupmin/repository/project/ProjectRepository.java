package com.hyupmin.repository.project;

import com.hyupmin.domain.project.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    // 참여 코드로 프로젝트 조회
    Optional<Project> findByJoinCode(String joinCode);
}