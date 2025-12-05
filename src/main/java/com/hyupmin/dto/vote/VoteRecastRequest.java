package com.hyupmin.dto.vote;

import lombok.Getter;

import java.util.List;

@Getter
public class VoteRecastRequest {
    // 이번에 최종적으로 선택된 옵션들의 ID 목록
    private List<Long> selectedOptionIds;
}
