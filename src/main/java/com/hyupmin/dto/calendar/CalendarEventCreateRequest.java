package com.hyupmin.dto.calendar;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList; // List import
import java.util.List; // List import

@Getter
@Setter
@NoArgsConstructor
public class CalendarEventCreateRequest {
    private String title;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String description;

    // 참가자 Pk 리스트 추가
    private List<Long> participantUserPks = new ArrayList<>();
}