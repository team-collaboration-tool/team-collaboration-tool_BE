package com.hyupmin.dto.post;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;


@Getter
@NoArgsConstructor
public class PostCreateRequest {

    // 1. 어느 프로젝트에 속한 게시글인지
    private Long projectPk;

    // 2. 게시글 자체의 정보
    private String title;
    private String content;

    // 3. 옵션 정보
    private Boolean isNotice;
    private Boolean hasVoting;

    // 4. 투표 정보
    private String voteTitle;
    private List<String> voteOptions;

    // 단일 투표, 중복 투표
    private Boolean allowMultipleChoices;

    // 익명 실명 투표
    private Boolean isAnonymous;

    //투표 마감 시간
    private String voteEndTime;
}