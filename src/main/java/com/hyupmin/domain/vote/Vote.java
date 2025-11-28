package com.hyupmin.domain.vote;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.hyupmin.domain.post.Post;

import java.time.LocalDateTime;
import java.util.ArrayList; // 리스트 초기화를 위해 필요
import java.util.List;

@Entity
@Table(name = "votes")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // [수정 1] votePk -> id (이제 getId()가 생성됨)

    @OneToOne
    @JoinColumn(name = "post_pk", nullable = false, unique = true)
    private Post post;

    @Column(nullable = false)
    private String title;

    // 투표 시작/종료 시간이 없다면 null일 수도 있으므로 상황에 따라 nullable 조정
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @Builder.Default
    private Boolean allowMultipleChoices = false;
    @Builder.Default
    private Boolean isAnonymous = false;

    // [수정 2] options -> voteOptions (이제 getVoteOptions()가 생성됨)
    // [수정 3] new ArrayList<>(); 로 초기화 (NullPointerException 방지)
    @OneToMany(mappedBy = "vote", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<VoteOption> voteOptions = new ArrayList<>();

    // 연관관계 편의 메서드 (Post 설정)
    public void setPost(Post post) {
        this.post = post;
    }

    // [수정 4] addOption 메서드 구현 (빈 껍데기 채우기)
    public void addOption(VoteOption option) {
        this.voteOptions.add(option);
        option.setVote(this);
    }
}