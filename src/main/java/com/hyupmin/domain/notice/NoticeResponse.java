package com.hyupmin.domain.notice;

import lombok.Builder;
import lombok.Getter;
import com.hyupmin.domain.notice.Notice;

import java.time.LocalDateTime;

@Getter
@Builder
public class NoticeResponse {
    private Long noticePk;
    private Long projectPk;
    private String createUserName;
    private String title;
    private String content;
    private LocalDateTime createdAt;

    public static NoticeResponse from(Notice notice) {
        return NoticeResponse.builder()
                .noticePk(notice.getNoticePk())
                .projectPk(notice.getProject().getProjectPk())
                .createUserName(notice.getCreateUser().getName())
                .title(notice.getTitle())
                .content(notice.getContent()) // USECASE #19에서는 제목만 필요하지만, 상세 조회를 위해 포함
                .createdAt(notice.getCreatedAt())
                .build();
    }
}