package com.hyupmin.controller.project;

import lombok.RequiredArgsConstructor;
import com.hyupmin.dto.project.*;
import com.hyupmin.service.project.ProjectService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    // 프로젝트 생성
    @PostMapping
    public ResponseEntity<ProjectResponseDTO> createProject(
            @RequestBody ProjectCreateRequest request,
            @AuthenticationPrincipal String email) { // JWT 토큰에서 사용자 이메일 추출

        ProjectResponseDTO response = projectService.createProject(request, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 참여 중인 프로젝트 목록 조회
    @GetMapping("/me")
    public ResponseEntity<List<ProjectListResponse>> getMyProjects(
            @AuthenticationPrincipal String email) {

        List<ProjectListResponse> response = projectService.getParticipatingProjects(email);
        return ResponseEntity.ok(response);
    }

    // 프로젝트 상세 조회 (참여 중인 멤버만 접근 가능하도록 Service에서 인가 체크)
    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectResponseDTO> getProject(
            @PathVariable Long projectId,
            @AuthenticationPrincipal String email) {

        ProjectResponseDTO response = projectService.getProject(projectId, email);
        return ResponseEntity.ok(response);
    }

    // 프로젝트 수정 (관리자 권한)
    @PutMapping("/{projectId}")
    public ResponseEntity<ProjectResponseDTO> updateProject(
            @PathVariable Long projectId,
            @RequestBody ProjectCreateRequest request,
            @AuthenticationPrincipal String email) {

        ProjectResponseDTO response = projectService.updateProject(projectId, request, email);
        return ResponseEntity.ok(response);
    }

    // 프로젝트 삭제 (관리자 권한)
    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> deleteProject(
            @PathVariable Long projectId,
            @RequestBody ProjectLeaveRequest request,
            @AuthenticationPrincipal String email) {

        projectService.deleteProject(projectId, email, request.getProjectName());
        return ResponseEntity.noContent().build();
    }

    // 프로젝트 참여 코드 조회 <- 관지라, 사용자 둘 다 프로젝트 관리에서 볼 수 있으니 필요 없을 듯
    @GetMapping("/{projectId}/join-code")
    public ResponseEntity<ProjectCodeResponse> getProjectCode(
            @PathVariable Long projectId,
            @AuthenticationPrincipal String email) {

        // Service에서 방장 권한 확인 후 JoinCode 반환 로직 구현 필요x
        ProjectCodeResponse response = projectService.getProjectJoinCode(projectId, email);
        return ResponseEntity.ok(response);
    }

    // 프로젝트 나가기 (멤버 권한, 프로젝트명 확인 필요)
    @DeleteMapping("/{projectId}/leave")
    public ResponseEntity<Void> leaveProject(
            @PathVariable Long projectId,
            @RequestBody ProjectLeaveRequest request, // 확인용 프로젝트 이름
            @AuthenticationPrincipal String email) {

        projectService.leaveProject(projectId, email, request.getProjectName());
        return ResponseEntity.noContent().build();
    }

    // 프로젝트 참여 요청 (참여 코드로 검색 및 PENDING 요청 생성)
    @PostMapping("/join-request")
    public ResponseEntity<ProjectJoinRequestResponse> requestJoin(
            @RequestParam("code") String joinCode, // /join-request?code="프로젝트 참여 코드"
            @AuthenticationPrincipal String email) {

        ProjectJoinRequestResponse response = projectService.requestJoin(joinCode, email);
        return ResponseEntity.ok(response);
    }

    // 프로젝트 참여 요청 목록 조회 (관리자 권한)
    @GetMapping("/{projectId}/join-requests")
    public ResponseEntity<List<ProjectJoinRequestResponse>> getPendingRequests(
            @PathVariable Long projectId,
            @AuthenticationPrincipal String email) {

        List<ProjectJoinRequestResponse> response = projectService.getPendingRequestsList(projectId, email);
        return ResponseEntity.ok(response);
    }

    // 프로젝트 참여 요청 승인 (관리자 권한)
    @PostMapping("/{projectId}/approve/{projectUserPk}")
    public ResponseEntity<ProjectResponseDTO> approveJoin(
            @PathVariable Long projectId,
            @PathVariable Long projectUserPk, // 승인할 요청의 ProjectUser PK
            @AuthenticationPrincipal String email) {

        ProjectResponseDTO response = projectService.approveProject(projectId, email, projectUserPk);
        return ResponseEntity.ok(response);
    }

    // 프로젝트 참여 요청 거절 (관리자 권한)
    @DeleteMapping("/{projectId}/join-requests/{projectUserPk}")
    public ResponseEntity<Void> rejectJoin(
            @PathVariable Long projectId,
            @PathVariable Long projectUserPk, // 거절할 요청의 ProjectUser PK
            @AuthenticationPrincipal String email) {

        projectService.rejectProjectJoin(projectId, projectUserPk, email);
        return ResponseEntity.noContent().build();
    }

    // 프로젝트 멤버 추방 구현 (관리자 권한)
    @DeleteMapping("/{projectId}/expel/{projectUserPk}")
    public ResponseEntity<Void> expel(
            @PathVariable Long projectId,
            @PathVariable Long projectUserPk,
            @AuthenticationPrincipal String email) {

        projectService.expelProjectMember(projectId, projectUserPk, email);
        return ResponseEntity.noContent().build();
    }

    // 프로젝트 관리자 권한 양도 (관리자 권한)
    @PutMapping("/{projectId}/transfer/{targetUserPk}")
    public ResponseEntity<Void> transferOwnership(
            @PathVariable Long projectId,
            @PathVariable Long targetUserPk,
            @AuthenticationPrincipal String email) {

        projectService.transferOwnership(projectId, targetUserPk, email);
        return ResponseEntity.noContent().build();
    }
}