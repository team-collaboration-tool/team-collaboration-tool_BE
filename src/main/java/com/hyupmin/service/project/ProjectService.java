package com.hyupmin.service.project;

import lombok.RequiredArgsConstructor;
import com.hyupmin.domain.project.Project;
import com.hyupmin.domain.projectUser.ProjectUser;
import com.hyupmin.domain.projectUser.ProjectUser.ProjectStatus;
import com.hyupmin.domain.projectUser.ProjectUser.ProjectRole;
import com.hyupmin.domain.user.User;
import com.hyupmin.dto.project.*;
import com.hyupmin.repository.project.ProjectRepository;
import com.hyupmin.repository.project.ProjectUserRepository;
import com.hyupmin.service.user.UserService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectUserRepository projectUserRepository;
    private final UserService userService;

    // 프로젝트 조회 편의 메서드
    public Project findProjectById(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 프로젝트입니다."));
    }

    // 프로젝트 생성
    @Transactional
    public ProjectResponseDTO createProject(ProjectCreateRequest request, String ownerEmail) {
        User owner = userService.findByEmail(ownerEmail); // UserService 클래스에서 정의한 email을 받아  owner객체에 저장.

        // Project 엔티티 생성 (생성자에서 joinCode 자동 생성)
        Project project = new Project(request.getProjectName(), owner); // 프로젝트 생성 시 입력한 name, description을 ProjectCreateRequest request(dto)에서 값을 가져옴.
        Project savedProject = projectRepository.save(project);

        // 프로젝트 생성자를 OWNER역할로 하고, 상태는 바로 APPROVED로 설정하여 프로젝트 참여를 완료 시킴.
        ProjectUser ownerMember = new ProjectUser(savedProject, owner, ProjectStatus.APPROVED, ProjectRole.OWNER);
        projectUserRepository.save(ownerMember);
        savedProject.getProjectUsers().add(ownerMember);

        return ProjectResponseDTO.from(savedProject, ownerMember);
    }

    // 현재 참여 중인 프로젝트 목록 조회
    @Transactional(readOnly = true)
    public List<ProjectListResponse> getParticipatingProjects(String userEmail) {
        User user = userService.findByEmail(userEmail); // 매개변수로 userEmail을 받아 DB에서 email 조회.

        // APPROVED 상태인 ProjectUser 목록만 조회
        List<ProjectUser> approvedProjects = projectUserRepository.findByUserAndStatus(user, ProjectStatus.APPROVED);

        return approvedProjects.stream()
                .map(ProjectUser::getProject) // Project 엔티티만 추출
                .map(ProjectListResponse::from) // ProjectResponse로 변환
                .collect(Collectors.toList());
    }

    // 단일 프로젝트 조회 <- 대시보드에서 해당 프로젝트 클릭시 사용자가 'APPROVED' 상태인지 권한 확인 후 프로젝트 기본 정보 반환.
    @Transactional(readOnly = true)
    public ProjectResponseDTO getProject(Long projectId, String userEmail) {
        Project project = findProjectById(projectId);
        User user = userService.findByEmail(userEmail);

        // 현재 접속자의 ProjectUser 정보 조회
        ProjectUser currentUserProjectUser = projectUserRepository.findByProjectAndUser(project, user)
                .orElseThrow(() -> new AccessDeniedException("해당 프로젝트에 참여 중이 아닙니다."));

        if (currentUserProjectUser.getStatus() != ProjectStatus.APPROVED) {
            throw new AccessDeniedException("해당 프로젝트에 참여 중이거나 승인 대기 중입니다.");
        }

        // ProjectUser 정보를 포함하여 DTO 변환
        return ProjectResponseDTO.from(project, currentUserProjectUser);
    }
    // 프로젝트 수정 <- 권한 확인 후 변경 가능.
    @Transactional
    public ProjectResponseDTO updateProject(Long projectId, ProjectCreateRequest request, String requesterEmail) { // requesterEmail: 수정을 하는 사용자의 권한 확인이 필요하기 때문에 매개변수로 받는다.
        Project project = findProjectById(projectId); // projectId를 통해 DB에서 조회.

        if (!project.getOwner().getEmail().equals(requesterEmail)) { // 프로젝트 방장이 아닌 경우 예외 처리.
            throw new AccessDeniedException("프로젝트 방장만 정보를 수정할 수 있습니다.");
        }

        project.update(request.getProjectName());
        return ProjectResponseDTO.from(project);
    }

    // 프로젝트 나가기 <- 사용자가 프로젝트를 나가기 위한 조건 검사.
    @Transactional
    public void leaveProject(Long projectId, String userEmail, String projectNameToConfirm) {
        Project project = findProjectById(projectId);
        User user = userService.findByEmail(userEmail);

        if (project.getOwner().getEmail().equals(userEmail)) {
            throw new AccessDeniedException("방장은 프로젝트를 나가려면 권한을 위임해야 합니다.");
        }

        if (!project.getName().equals(projectNameToConfirm)) {
            throw new IllegalArgumentException("입력한 프로젝트명이 일치하지 않습니다.");
        }

        ProjectUser projectUser = projectUserRepository.findByProjectAndUser(project, user)
                .orElseThrow(() -> new IllegalArgumentException("해당 프로젝트에 참여 중인 사용자가 아닙니다."));

        projectUserRepository.delete(projectUser);
    }

    // 프로젝트 삭제 <- 권한 확인 후 삭제 가능.
    @Transactional
    public void deleteProject(Long projectId, String requesterEmail, String projectNameToConfirm) { // 권한 확인을 위해 userEmail을 받음.
        Project project =  findProjectById(projectId);

        if (!project.getOwner().getEmail().equals(requesterEmail)) { // 방장이 아닌 경우 예외 처리.
            throw new AccessDeniedException("프로젝트 방장만 프로젝트를 삭제할 수 있습니다.");
        }

        if (!project.getName().equals(projectNameToConfirm)) {
            throw new IllegalArgumentException("입력한 프로젝트명이 일치하지 않습니다.");
        }

        //프로젝트에 'APPROVE' 상태인 사용자가 자기 자신뿐인 경우 삭제 처리 (다른 사용자도 있을 경우 예외 처리 필요).

        projectRepository.delete(project);
    }

    // 프로젝트 코드 확인 (방장 권한)
    @Transactional(readOnly = true)
    public ProjectCodeResponse getProjectJoinCode(Long projectId, String requesterEmail) {
        Project project = findProjectById(projectId);
        User requester = userService.findByEmail(requesterEmail);

        // 1. 프로젝트에 참여 중인지 확인
        ProjectUser projectUser = projectUserRepository.findByProjectAndUser(project, requester)
                .orElseThrow(() -> new AccessDeniedException("프로젝트에 참여 중인 멤버가 아닙니다."));

        // 2. 참여가 '승인(APPROVED)'되었는지 확인
        if (projectUser.getStatus() != ProjectStatus.APPROVED) {
            throw new AccessDeniedException("프로젝트 참여 승인을 받은 멤버만 코드를 조회할 수 있습니다.");
        }

        return ProjectCodeResponse.builder()
                .projectPk(project.getProjectPk())
                .projectName(project.getName())
                .joinCode(project.getJoinCode())
                .build();
    }


    // 참여 코드 검색 및 참여 요청
    @Transactional
    public ProjectJoinRequestResponse requestJoin(String joinCode, String requesterEmail) {
        Project project = projectRepository.findByJoinCode(joinCode)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 참여 코드입니다."));

        User requester = userService.findByEmail(requesterEmail);

        // 2. 이미 요청했거나 참여 중인지 확인
        if (projectUserRepository.existsByProjectAndUser(project, requester)) {
            throw new IllegalArgumentException("이미 참여 요청했거나 참여 중인 프로젝트입니다.");
        }

        ProjectUser projectUser = new ProjectUser(project, requester, ProjectStatus.PENDING, ProjectRole.MEMBER); // ProjectUser 생성자를 이용해서 요청 정보 저장.
        projectUserRepository.save(projectUser);

        // 요청 결과를 DTO로 반환
        return ProjectJoinRequestResponse.builder()
                .projectPk(project.getProjectPk())
                .projectName(project.getName())
                .requesterEmail(requesterEmail)
                .status(ProjectStatus.PENDING.name())
                .message("프로젝트 방장에게 참여 요청을 보냈습니다. 승인을 기다려주세요.")
                .build();
    }

    // 프로젝트 참여 요청 승인.
    @Transactional
    public ProjectResponseDTO approveProject(Long projectId, String requesterEmail, Long projectUserPk) { // 어떤 프로젝트인지 누가 요청한지. + 해당 프로젝트에 요청한 유저가 존재하는지 확인을 위한 projectUserPk.

        Project project = findProjectById(projectId);

        ProjectUser projectUser = projectUserRepository.findById(projectUserPk)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 참여 요청 정보입니다."));

        projectUser.approve();

        return ProjectResponseDTO.from(project);
    }

    // 프로젝트 참여 요청 거절 (방장 권한)
    @Transactional
    public void rejectProjectJoin(Long projectId, Long projectUserPk, String ownerEmail) {
        Project project = findProjectById(projectId);

        // 요청자가 프로젝트 방장인지 확인
        if (!project.getOwner().getEmail().equals(ownerEmail)) {
            throw new AccessDeniedException("프로젝트 소유자만 참여 요청을 거절할 수 있습니다.");
        }

        // 거절할 요청 (ProjectUser 엔티티) 조회
        ProjectUser projectUser = projectUserRepository.findById(projectUserPk)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 참여 요청 정보입니다."));

        // 요청이 해당 프로젝트에 대한 것인지 확인
        if (!projectUser.getProject().getProjectPk().equals(projectId)) {
            throw new IllegalArgumentException("해당 요청은 이 프로젝트에 대한 요청이 아닙니다.");
        }

        // 상태가 PENDING이 아니면 거절할 수 없음 (선택적 검증)
        if (projectUser.getStatus() != ProjectStatus.PENDING) {
            throw new IllegalArgumentException("승인 대기 상태의 요청만 거절할 수 있습니다.");
        }

        // ProjectUser 삭제 (거절 처리)
        projectUserRepository.delete(projectUser);
    }

    // 승인 대기 중인 요청 목록 조회 <- 방장의 설정 페이지 창 요청에 사용.
    @Transactional(readOnly = true)
    public List<ProjectJoinRequestResponse> getPendingRequestsList(Long projectId, String ownerEmail) {
        Project project = findProjectById(projectId);

        // 방장인지 권한 확인.
        if (!project.getOwner().getEmail().equals(ownerEmail)) {
            throw new AccessDeniedException("프로젝트 소유자만 요청 목록을 조회할 수 있습니다.");
        }

        List<ProjectUser> pendingRequests = projectUserRepository.findByProjectAndStatus(project, ProjectStatus.PENDING);

        return pendingRequests.stream()
                .map(req -> ProjectJoinRequestResponse.builder()
                        .projectUserPk(req.getProjectUserPk())
                        .projectName(project.getName())
                        .requesterEmail(req.getUser().getEmail())
                        .requesterName(req.getUser().getName())
                        .status(req.getStatus().name())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public void expelProjectMember(Long projectId, Long projectUserPk, String ownerEmail) {
        Project project = findProjectById(projectId);

        if (!project.getOwner().getEmail().equals(ownerEmail)) {
            throw new AccessDeniedException("프로젝트 방장만 멤버를 추방할 수 있습니다.");
        }

        ProjectUser memberToExpel = projectUserRepository.findById(projectUserPk)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 프로젝트 참여 정보입니다."));

        if (memberToExpel.getRole() == ProjectRole.OWNER) {
            throw new IllegalArgumentException("프로젝트 소유자는 자신을 추방할 수 없습니다.");
        }

        projectUserRepository.delete(memberToExpel);
    }

    // 프로젝트 관리자 권한 양도
    @Transactional
    public void transferOwnership(Long projectId, Long targetUserPk, String currentOwnerEmail) {
        Project project = findProjectById(projectId);
        User currentOwner = userService.findByEmail(currentOwnerEmail);

        // 권한 확인
        if (!project.getOwner().getEmail().equals(currentOwnerEmail)) {
            throw new AccessDeniedException("프로젝트 방장만 권한을 양도할 수 있습니다.");
        }

        // 양도 대상 멤버 조회
        ProjectUser targetMember = projectUserRepository.findById(targetUserPk)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 프로젝트 참여자입니다."));

        // 같은 프로젝트인지 검증
        if (!targetMember.getProject().getProjectPk().equals(projectId)) {
            throw new IllegalArgumentException("해당 멤버는 이 프로젝트에 속해 있지 않습니다.");
        }

        // 현재 승인된 멤버인지 확인
        if (targetMember.getStatus() != ProjectUser.ProjectStatus.APPROVED) {
            throw new IllegalArgumentException("아직 승인되지 않은 멤버에게 권한을 양도할 수 없습니다.");
        }

        // 기존 OWNER ProjectUser 찾기
        ProjectUser currentOwnerPk = projectUserRepository.findByProjectAndUser(project, currentOwner)
                .orElseThrow(() -> new IllegalArgumentException("현재 방장 정보를 찾을 수 없습니다."));

        // 역할 변경
        currentOwnerPk.changeRole(ProjectUser.ProjectRole.MEMBER);
        targetMember.changeRole(ProjectUser.ProjectRole.OWNER);


        // 프로젝트 엔티티의 owner 필드 변경
        project.changeOwner(targetMember.getUser());

        projectRepository.save(project);
    }
}