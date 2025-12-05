package com.hyupmin.controller.timepoll;

import lombok.RequiredArgsConstructor;
import com.hyupmin.dto.timepoll.TimePollDto;
import com.hyupmin.service.timepoll.TimePollService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/time-poll")
@RequiredArgsConstructor
public class TimePollController {

    private final TimePollService timePollService;

    /**
     * 1. 페이지 진입 시: 프로젝트별 전체 시간조율 목록 조회
     */
    @GetMapping("/list/{projectId}")
    public ResponseEntity<List<TimePollDto.PollSummary>> getAllPolls(@PathVariable Long projectId) {
        List<TimePollDto.PollSummary> pollList = timePollService.getPollList(projectId);
        return ResponseEntity.ok(pollList);
    }

    /**
     * 2. 시간조율 생성
     * - 생성 후 해당 프로젝트의 최신 목록 반환
     * - 현재는 디버깅을 위해 try-catch 로 예외 내용을 그대로 내려줌
     */
    @PostMapping
    public ResponseEntity<?> createPoll(@RequestBody TimePollDto.CreateRequest request) {
        try {
            timePollService.createTimePoll(request);

            List<TimePollDto.PollSummary> pollList = timePollService.getPollList(request.getProjectId());
            return ResponseEntity.ok(pollList);

        } catch (Exception e) {
            e.printStackTrace();

            String message = e.getClass().getSimpleName() +
                    " : " + (e.getMessage() != null ? e.getMessage() : "no message");
            return ResponseEntity.internalServerError().body(message);
        }
    }

    /**
     * 3. 특정 시간조율 상세 조회 (히트맵 포함)
     * - 팀 전체 grid + 내 grid 둘 다 반환
     * - ex) GET /api/time-poll/1?userId=3
     */
    @GetMapping("/{pollId}")
    public ResponseEntity<TimePollDto.DetailResponse> getPollDetail(
            @PathVariable Long pollId,
            @RequestParam Long userId
    ) {
        TimePollDto.DetailResponse detail = timePollService.getPollDetailGrid(pollId, userId);
        return ResponseEntity.ok(detail);
    }

    /**
     * 4. 사용자가 드래그로 선택한 시간대 제출
     * - 응답 저장 후, 업데이트된 히트맵 다시 반환
     */
    @PostMapping("/submit")
    public ResponseEntity<TimePollDto.DetailResponse> submitTime(
            @RequestBody TimePollDto.SubmitRequest request
    ) {
        timePollService.submitResponse(request);
        TimePollDto.DetailResponse updatedDetail =
                timePollService.getPollDetailGrid(request.getPollId(), request.getUserId());
        return ResponseEntity.ok(updatedDetail);
    }
}
