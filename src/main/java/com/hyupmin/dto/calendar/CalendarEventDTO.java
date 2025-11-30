package com.hyupmin.dto.calendar;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class CalendarEventDTO {
    @NotBlank (message = "제목을 입력하세요.")
    @Size (max = 25)
    private String title;

    private Long categoryId;

    @NotNull (message = "시작 시간을 선택하세요.")
    Instant startTime;

    @NotNull(message = "종료 시간을 선택하세요..")
    Instant endTime;

    @Size (max = 500, message = "상세 정보는 500자를 넘을 수 없습니다.")
    private String description;

    List<Long> participantUserPks;
}
