package com.hyupmin.controller.notice;

import lombok.RequiredArgsConstructor;
import com.hyupmin.domain.notice.NoticeResponse;
import com.hyupmin.service.notice.NoticeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/projects/{projectId}/notices") // 프로젝트별 공지사항
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    // USECASE #19: 공지사항 목록 조회
    @GetMapping
    public ResponseEntity<List<NoticeResponse>> getNotices(
            @PathVariable Long projectId,
            @AuthenticationPrincipal String email) {

        List<NoticeResponse> response = noticeService.getNoticesByProject(projectId, email);
        return ResponseEntity.ok(response);
    }

    // (참고) 공지사항 생성 API
    @PostMapping
    public ResponseEntity<NoticeResponse> createNotice(
            @PathVariable Long projectId,
            @RequestBody Map<String, String> request,
            @AuthenticationPrincipal String email) {

        String title = request.get("title");
        String content = request.get("content");

        NoticeResponse response = noticeService.createNotice(projectId, title, content, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}