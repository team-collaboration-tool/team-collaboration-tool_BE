package com.hyupmin.dto.vote;


import lombok.Getter;
import com.hyupmin.domain.vote.Vote;
import com.hyupmin.domain.vote.VoteOption;
import com.hyupmin.domain.vote.VoteRecord;
import com.hyupmin.domain.user.User;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.Collections;

@Getter
public class VoteResponse {
    private Long id;
    private String title;
    private List<VoteOptionDto> options;

    // true  -> 중복 선택
    // false -> 단일 선택
    private Boolean allowMultipleChoices;

    // true  -> 익명 투표
    // false -> 실명 투표
    private Boolean isAnonymous;


    private boolean hasVoted; //로그인 사용자 해당 투표 참여 여부

    public VoteResponse(Vote vote) {
        this.id = vote.getId();
        this.title = vote.getTitle();
        this.options = vote.getVoteOptions().stream()
                .map(VoteOptionDto::new)
                .collect(Collectors.toList());
        this.allowMultipleChoices = Boolean.TRUE.equals(vote.getAllowMultipleChoices());
        this.isAnonymous = Boolean.TRUE.equals(vote.getIsAnonymous());
        this.hasVoted = false;
    }

    public VoteResponse(Vote vote, boolean hasVoted) {
        this.id = vote.getId();
        this.title = vote.getTitle();
        this.options = vote.getVoteOptions().stream()
                .map(VoteOptionDto::new)
                .collect(Collectors.toList());

        this.allowMultipleChoices = Boolean.TRUE.equals(vote.getAllowMultipleChoices());
        this.isAnonymous = Boolean.TRUE.equals(vote.getIsAnonymous());
        this.hasVoted = hasVoted;
    }

    public VoteResponse(Vote vote, boolean hasVoted, List<VoteRecord> voteRecords) {
        this.id = vote.getId();
        this.title = vote.getTitle();

        this.allowMultipleChoices = Boolean.TRUE.equals(vote.getAllowMultipleChoices());
        this.isAnonymous = Boolean.TRUE.equals(vote.getIsAnonymous());
        this.hasVoted = hasVoted;

        Map<Long, List<String>> votersByOptionId = voteRecords.stream()
                .collect(Collectors.groupingBy(
                        vr -> vr.getVoteOption().getId(),
                        Collectors.mapping(
                                vr -> vr.getUser().getName(),
                                Collectors.toList()
                        )
                ));

        this.options = vote.getVoteOptions().stream()
                .map(option -> new VoteOptionDto(
                        option,
                        votersByOptionId.getOrDefault(option.getId(), Collections.emptyList())
                ))
                .collect(Collectors.toList());
    }

    @Getter
    public static class VoteOptionDto {
        private Long id;
        private String content;
        private Integer count;

        private List<String> voters;

        // 기존 용도(익명 투표나 목록 조회용)
        public VoteOptionDto(VoteOption option) {
            this(option, null);
        }

        public VoteOptionDto(VoteOption option, List<String> voters) {
            this.id = option.getId();
            this.content = option.getContent();
            this.count = option.getCount();
            this.voters = voters;
        }
    }
}