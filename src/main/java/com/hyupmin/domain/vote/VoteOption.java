package com.hyupmin.domain.vote;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteOption {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content; // 항목 내용 (예: "A안", "B안")

    @Builder.Default
    private Integer count = 0; // 득표수

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vote_id")
    private Vote vote;

    // 연관관계 설정
    public void setVote(Vote vote) {
        this.vote = vote;
    }

    // 투표하기 (카운트 증가)
    public void increaseCount() {
        this.count++;
    }
    //기존 기록 삭제
    public void decreaseCount() {
        if (this.count > 0) {
            this.count--;
        }
    }
}