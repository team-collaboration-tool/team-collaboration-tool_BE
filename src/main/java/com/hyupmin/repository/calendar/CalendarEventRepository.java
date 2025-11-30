package com.hyupmin.repository.calendar;

import com.hyupmin.domain.calendar.CalendarEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime; // LocalDateTime import
import java.util.List;

public interface CalendarEventRepository extends JpaRepository<CalendarEvent, Long> {

    List<CalendarEvent> findByProject_ProjectPkIn(List<Long> projectPks);

    // 특정 프로젝트에 속한 모든 일정 조회 (달력 페이지에서 사용)
    List<CalendarEvent> findByProject_ProjectPk(Long projectPk);

    // 7일 이내 마감 일정 조회
    List<CalendarEvent> findByProject_ProjectPkAndEndTimeBetween(Long projectPk, LocalDateTime start, LocalDateTime end);
}