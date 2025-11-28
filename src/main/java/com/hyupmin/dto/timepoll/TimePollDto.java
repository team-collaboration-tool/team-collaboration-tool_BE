package com.hyupmin.dto.timepoll;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class TimePollDto {

    // 1. [생성 요청] 날짜 + "몇 일치(duration)"를 받음
    @Data
    public static class CreateRequest {
        private Long projectId;
        private Long creatorId;
        private String title;
        private LocalDate startDate;
        private Integer duration; // 예: 3일치면 3
        private LocalTime startTimeOfDay;
        private LocalTime endTimeOfDay;
    }

    // 2. [목록 조회용 응답] 리스트에 뿌려줄 간단한 정보
    @Data
    @Builder
    public static class PollSummary {
        private Long pollId;
        private String title;
        private LocalDate startDate;
        private LocalDate endDate;
        private Integer userCount; // 참여자 수 등 (옵션)
    }

    // 3. [제출 요청] 내 시간표 제출
    @Data
    public static class SubmitRequest {
        private Long pollId;
        private Long userId;
        private List<TimeRange> availableTimes; // 내가 선택한 시간 구간들
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TimeRange {
        private String start; // LocalDateTime 대신 String (ISO-8601) 추천
        private String end;
    }

    // 4. [상세/히트맵 응답] 2차원 배열 데이터 포함!
    @Data
    @Builder
    public static class DetailResponse {
        private Long pollId;
        private String title;
        // 2차원 배열: [날짜_인덱스][시간_슬롯_인덱스] = 가능 인원 수
        // 예: gridData[0][2] -> 첫째날, 3번째 시간타임(예: 10:00~10:30)에 가능한 사람 수
        private int[][] gridData;
        private List<String> dateLabels; // ["11-18", "11-19", ...]
        private List<String> timeLabels; // ["09:00", "09:30", ...]
    }
}