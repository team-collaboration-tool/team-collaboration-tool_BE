package com.hyupmin.service.notice;

import lombok.RequiredArgsConstructor;
import com.hyupmin.domain.notice.Notice;
import com.hyupmin.domain.project.Project;
import com.hyupmin.domain.projectUser.ProjectUser;
import com.hyupmin.domain.projectUser.ProjectUser.ProjectStatus;
import com.hyupmin.domain.user.User;
import com.hyupmin.domain.notice.NoticeResponse;
import com.hyupmin.repository.notice.NoticeRepository;
import com.hyupmin.repository.project.ProjectUserRepository;
import com.hyupmin.service.project.ProjectService;
import com.hyupmin.service.user.UserService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final ProjectService projectService;
    private final UserService userService;
    private final ProjectUserRepository projectUserRepository;

    // 프로젝트 참여 권한 확인
    private void checkProjectMembership(Project project, User user) {
        ProjectUser projectUser = projectUserRepository.findByProjectAndUser(project, user)
                .orElseThrow(() -> new AccessDeniedException("프로젝트에 참여 중이 아닙니다."));

        if (projectUser.getStatus() != ProjectStatus.APPROVED) {
            throw new AccessDeniedException("프로젝트 참여 승인을 기다려야 합니다.");
        }
    }

    // 공지사항 목록 조회
    @Transactional(readOnly = true)
    public List<NoticeResponse> getNoticesByProject(Long projectId, String userEmail) {
        Project project = projectService.findProjectById(projectId);
        User user = userService.findByEmail(userEmail);

        checkProjectMembership(project, user);

        List<Notice> notices = noticeRepository.findByProject_ProjectPkOrderByCreatedAtDesc(projectId);

        return notices.stream()
                .map(NoticeResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public NoticeResponse createNotice(Long projectId, String title, String content, String userEmail) {
        Project project = projectService.findProjectById(projectId);
        User user = userService.findByEmail(userEmail);

        Notice notice = new Notice(project, user, title, content);
        Notice savedNotice = noticeRepository.save(notice);
        return NoticeResponse.from(savedNotice);
    }
}