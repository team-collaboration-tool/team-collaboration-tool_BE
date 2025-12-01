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
    private String color;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String description;

    private List<Long> participantUserPks = new ArrayList<>();
}