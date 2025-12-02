package com.hyupmin.dto.vote;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class VoteCreateRequest {

    private Long postId; // 어느 게시글에 붙는 투표인지

    private String title; // 투표 제목

    private LocalDateTime endTime; // 마감 일시

    private Boolean allowMultipleChoices; // 중복 선택 허용 여부

    private Boolean isAnonymous; // 익명 투표 여부

    // 선택지 내용들 (선택지1~6, + 추가 버튼에서 온 값들)
    private List<String> optionContents;
}