package com.hyupmin.dto.post;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class PostUpdateRequest {
    private String title;
    private String content;
    private Boolean isNotice;

    private Boolean hasVoting;          // 수정 후 이 글에 투표를 둘지 여부
    private String voteTitle;           // (선택) 투표 제목 수정
    private List<String> voteOptions;   // (선택) 옵션 전체 갈아끼우기
    private Boolean allowMultipleChoices;
    private Boolean isAnonymous;
    private LocalDateTime voteEndTime;
}