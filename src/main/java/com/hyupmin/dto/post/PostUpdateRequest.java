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

    private Boolean hasVoting;
    private String voteTitle;
    private List<String> voteOptions;
    private Boolean allowMultipleChoices;
    private Boolean isAnonymous;
    private LocalDateTime voteEndTime;
}