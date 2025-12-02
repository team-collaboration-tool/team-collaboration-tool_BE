package com.hyupmin.dto.vote;

import lombok.Getter;
import com.hyupmin.domain.vote.Vote;
import com.hyupmin.domain.vote.VoteOption;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class VoteResponse {
    private Long id;
    private String title;
    private List<VoteOptionDto> options; // 아래 내부 클래스 사용

    private boolean hasVoted; //로그인 사용자 해당 투표 참여 여부

    // Entity -> DTO 변환 생성자
    public VoteResponse(Vote vote) {
        this.id = vote.getId();
        this.title = vote.getTitle();
        this.options = vote.getVoteOptions().stream()
                .map(VoteOptionDto::new)
                .collect(Collectors.toList());
        this.hasVoted = false;
    }

    public VoteResponse(Vote vote, boolean hasVoted) {
        this.id = vote.getId();
        this.title = vote.getTitle();
        this.options = vote.getVoteOptions().stream()
                .map(VoteOptionDto::new)
                .collect(Collectors.toList());
        this.hasVoted = hasVoted;
    }

    @Getter
    public static class VoteOptionDto {
        private Long id;
        private String content;
        private Integer count;

        public VoteOptionDto(VoteOption option) {
            this.id = option.getId();
            this.content = option.getContent();
            this.count = option.getCount();
        }
    }
}