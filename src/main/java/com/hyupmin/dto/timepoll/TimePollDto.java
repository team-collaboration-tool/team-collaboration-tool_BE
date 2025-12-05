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
        private Integer duration;
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
        private Integer duration;
        private LocalTime startTime;
        private LocalTime endTime;
        private Integer userCount;
    }

    // 3. [제출 요청] 내 시간표 제출
    @Data
    public static class SubmitRequest {
        private Long pollId;
        private Long userId;
        private List<TimeRange> availableTimes;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TimeRange {
        private String start;
        private String end;
    }

    // 4. [상세/히트맵 응답]
    @Data
    @Builder
    public static class DetailResponse {
        private Long pollId;
        private String title;
        private int[][] teamGrid;
        private int[][] myGrid;
        private List<String> dateLabels;
        private List<String> timeLabels;
    }
}