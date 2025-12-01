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
    private Long id;

    @OneToOne
    @JoinColumn(name = "post_pk", nullable = false, unique = true)
    private Post post;

    @Column(nullable = false)
    private String title;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @Builder.Default
    private Boolean allowMultipleChoices = false;

    @Builder.Default
    private Boolean isAnonymous = false;

    @OneToMany(mappedBy = "vote", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<VoteOption> voteOptions = new ArrayList<>();

    // === 연관관계 편의 메서드 ===
    public void setPost(Post post) {
        this.post = post;
    }

    public void addOption(VoteOption option) {
        this.voteOptions.add(option);
        option.setVote(this);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void setAllowMultipleChoices(Boolean allowMultipleChoices) {
        this.allowMultipleChoices = allowMultipleChoices;
    }

    public void setIsAnonymous(Boolean isAnonymous) {
        this.isAnonymous = isAnonymous;
    }

    public void clearOptions() {
        this.voteOptions.clear(); // orphanRemoval 때문에 기존 옵션들 삭제됨
    }
}
