package com.hyupmin.service.calendar;

import lombok.RequiredArgsConstructor;
import com.hyupmin.domain.calendar.CalendarEvent;
import com.hyupmin.domain.project.Project;
import com.hyupmin.domain.projectUser.ProjectUser;
import com.hyupmin.domain.projectUser.ProjectUser.ProjectStatus;
import com.hyupmin.domain.user.User;
import com.hyupmin.dto.calendar.CalendarEventCreateRequest;
import com.hyupmin.dto.calendar.CalendarEventResponse;
import com.hyupmin.repository.calendar.CalendarEventRepository;
import com.hyupmin.repository.project.ProjectUserRepository;
import com.hyupmin.repository.user.UserRepository; // ✅ UserRepository 주입
import com.hyupmin.service.project.ProjectService;
import com.hyupmin.service.user.UserService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime; // LocalDateTime import
import java.util.HashSet; // HashSet import
import java.util.List;
import java.util.Set; // Set import
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CalendarEventService {

    private final CalendarEventRepository calendarEventRepository;
    private final ProjectService projectService;
    private final UserService userService;
    private final ProjectUserRepository projectUserRepository;
    private final UserRepository userRepository;

    // 일정 엔티티 조회 편의 메서드
    private CalendarEvent findEventById(Long eventId) {
        return calendarEventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 일정입니다."));
    }

    // 전체 프로젝트 일정 조회
    @Transactional (readOnly = true)
    public List<CalendarEventResponse> getAllMyEvents(String userEmail) {
        User user = userService.findByEmail(userEmail);

        List<ProjectUser> approvedProjectsUsers = projectUserRepository.findByUserAndStatus(user, ProjectStatus.APPROVED);

        List<Long> projectIds = approvedProjectsUsers.stream()
                .map(ProjectUser::getProject) // ProjectUser에서 Project 엔티티 추출
                .map(Project::getProjectPk)   // Project 엔티티에서 projectPk 추출
                .collect(Collectors.toList());

        List<CalendarEvent> allEvents = calendarEventRepository.findByProject_ProjectPkIn(projectIds);

        return allEvents.stream()
                .map(CalendarEventResponse::from)
                .collect(Collectors.toList());
    }

    // 프로젝트 참여 권한 확인
    private void checkProjectMembership(Project project, User user) {
        ProjectUser projectUser = projectUserRepository.findByProjectAndUser(project, user)
                .orElseThrow(() -> new AccessDeniedException("프로젝트에 참여 중이 아닙니다."));

        if (projectUser.getStatus() != ProjectStatus.APPROVED) {
            throw new AccessDeniedException("프로젝트 참여 승인을 기다려야 합니다.");
        }
    }

    // 참가자 PK 리스트로 User Set 조회
    private Set<User> findParticipantsByPks(List<Long> participantUserPks) {
        if (participantUserPks == null || participantUserPks.isEmpty()) {
            return new HashSet<>();
        }
        return new HashSet<>(userRepository.findAllById(participantUserPks));
    }


    //  일정 등록 (수정됨)
    @Transactional
    public CalendarEventResponse createEvent(Long projectId, CalendarEventCreateRequest request, String userEmail) {
        Project project = projectService.findProjectById(projectId);
        User user = userService.findByEmail(userEmail);

        // 프로젝트 멤버만 일정 등록 가능
        checkProjectMembership(project, user);

        // 참가자 조회
        Set<User> participants = findParticipantsByPks(request.getParticipantUserPks());

        CalendarEvent event = new CalendarEvent(
                project,
                user,
                request.getTitle(),
                request.getStartTime(),
                request.getEndTime(),
                request.getDescription()
        );
        event.setParticipants(participants); // ✅ 참가자 설정

        CalendarEvent savedEvent = calendarEventRepository.save(event);
        return CalendarEventResponse.from(savedEvent);
    }

    // 특정 프로젝트의 일정 조회
    @Transactional(readOnly = true)
    public List<CalendarEventResponse> getEventsByProject(Long projectId, String userEmail) {
        Project project = projectService.findProjectById(projectId);
        User user = userService.findByEmail(userEmail);

        // 프로젝트 멤버만 일정 조회 가능
        checkProjectMembership(project, user);

        List<CalendarEvent> events = calendarEventRepository.findByProject_ProjectPk(projectId);

        return events.stream()
                .map(CalendarEventResponse::from)
                .collect(Collectors.toList());
    }

    // 7일 이내 마감 일정 조회 (신규)
    @Transactional(readOnly = true)
    public List<CalendarEventResponse> getUpcomingEvents(Long projectId, String userEmail) {
        Project project = projectService.findProjectById(projectId);
        User user = userService.findByEmail(userEmail);

        // 프로젝트 멤버만 조회 가능
        checkProjectMembership(project, user);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sevenDaysLater = now.plusDays(7);

        // 마감일(endTime)이 지금부터 7일 이내인 일정 조회
        List<CalendarEvent> events = calendarEventRepository.findByProject_ProjectPkAndEndTimeBetween(projectId, now, sevenDaysLater);

        return events.stream()
                .map(CalendarEventResponse::from)
                .collect(Collectors.toList());
    }

    // 단일 일정 상세 조회
    @Transactional(readOnly = true)
    public CalendarEventResponse getEvent(Long eventId, String userEmail) {
        CalendarEvent event = findEventById(eventId);
        User user = userService.findByEmail(userEmail);

        // 해당 일정이 속한 프로젝트의 멤버만 상세 조회 가능
        checkProjectMembership(event.getProject(), user);

        return CalendarEventResponse.from(event);
    }

    // 일정 수정
    @Transactional
    public CalendarEventResponse updateEvent(Long eventId, CalendarEventCreateRequest request, String userEmail) {
        CalendarEvent event = findEventById(eventId);
        User user = userService.findByEmail(userEmail); // 권한 확인용

        // 등록자 또는 참가자만 수정 가능
        if (!event.getCreateUser().getEmail().equals(userEmail) && !event.isParticipant(userEmail)) {
            throw new AccessDeniedException("일정을 등록한 사용자 또는 참가자만 수정할 수 있습니다.");
        }

        // 참가자 목록 조회 및 업데이트
        Set<User> participants = findParticipantsByPks(request.getParticipantUserPks());

        event.update(
                request.getTitle(),
                request.getStartTime(),
                request.getEndTime(),
                request.getDescription(),
                participants
        );

        return CalendarEventResponse.from(event);
    }

    // 일정 삭제
    @Transactional
    public void deleteEvent(Long eventId, String userEmail) {
        CalendarEvent event = findEventById(eventId);
        User user = userService.findByEmail(userEmail); // 권한 확인용

        // 등록자 또는 참가자만 삭제 가능
        if (!event.getCreateUser().getEmail().equals(userEmail) && !event.isParticipant(userEmail)) {
            throw new AccessDeniedException("일정을 등록한 사용자 또는 참가자만 삭제할 수 있습니다.");
        }

        calendarEventRepository.delete(event);
    }
}