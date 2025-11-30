package com.hyupmin.controller.calendar;

import lombok.RequiredArgsConstructor;
import com.hyupmin.dto.calendar.CalendarEventCreateRequest;
import com.hyupmin.dto.calendar.CalendarEventResponse;
import com.hyupmin.service.calendar.CalendarEventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/calendar") // 프로젝트별 경로
@RequiredArgsConstructor
public class CalendarController {

    private final CalendarEventService calendarEventService;

    // 전체 프로젝트 일정 조회 (홈페이지)
    @GetMapping("/me")
    public ResponseEntity<List<CalendarEventResponse>> getAllEventByProject(
            @AuthenticationPrincipal String email) {

        List<CalendarEventResponse> response = calendarEventService.getAllMyEvents(email);
        return ResponseEntity.ok(response);
    }


    // 단일 프로젝트 일정 조회 (달력 페이지)
    @GetMapping ("/projects/{projectId}")
    public ResponseEntity<List<CalendarEventResponse>> getEventsByProject(
            @PathVariable Long projectId,
            @AuthenticationPrincipal String email) { // 프로젝트 멤버 권한 확인용

        List<CalendarEventResponse> response = calendarEventService.getEventsByProject(projectId, email);
        return ResponseEntity.ok(response);
    }

    // 일정 추가
    @PostMapping("/projects/{projectId}")
    public ResponseEntity<CalendarEventResponse> createEvent(
            @PathVariable Long projectId,
            @RequestBody CalendarEventCreateRequest request,
            @AuthenticationPrincipal String email) { // 등록자 이메일

        CalendarEventResponse response = calendarEventService.createEvent(projectId, request, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    // 7일 이내 마감 일정 조회 (신규)
    @GetMapping("/upcoming")
    public ResponseEntity<List<CalendarEventResponse>> getUpcomingEvents(
            @PathVariable Long projectId,
            @AuthenticationPrincipal String email) {

        List<CalendarEventResponse> response = calendarEventService.getUpcomingEvents(projectId, email);
        return ResponseEntity.ok(response);
    }

    // 단일 일정 상세 조회
    @GetMapping("/project/{projectId}/{eventId}")
    public ResponseEntity<CalendarEventResponse> getEvent(
            @PathVariable Long projectId, // 경로 일관성 유지
            @PathVariable Long eventId,
            @AuthenticationPrincipal String email) {

        CalendarEventResponse response = calendarEventService.getEvent(eventId, email);
        return ResponseEntity.ok(response);
    }

    // 일정 수정
    @PutMapping("/projects/{projectId}/{eventId}")
    public ResponseEntity<CalendarEventResponse> updateEvent(
            @PathVariable Long projectId, // 경로 일관성 유지
            @PathVariable Long eventId,
            @RequestBody CalendarEventCreateRequest request,
            @AuthenticationPrincipal String email) { // 수정 권한 확인용

        CalendarEventResponse response = calendarEventService.updateEvent(eventId, request, email);
        return ResponseEntity.ok(response);
    }

    // 일정 삭제
    @DeleteMapping("/projects/{projectId}/{eventId}")
    public ResponseEntity<Void> deleteEvent(
            @PathVariable Long projectId, // 경로 일관성 유지
            @PathVariable Long eventId,
            @AuthenticationPrincipal String email) { // 삭제 권한 확인용

        calendarEventService.deleteEvent(eventId, email);
        return ResponseEntity.noContent().build();
    }
}