package com.hyupmin.domain.post;

import jakarta.persistence.*;
import lombok.*;
import com.hyupmin.domain.attachmentFile.AttachmentFile;
import com.hyupmin.domain.project.Project;
import com.hyupmin.domain.user.User;
import com.hyupmin.domain.BaseTimeEntity;
import com.hyupmin.domain.vote.Vote;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "posts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postPk;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_pk", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_pk", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<AttachmentFile> attachmentFiles = new ArrayList<>();

    @Builder.Default
    private Boolean isNotice = false;

    @Builder.Default
    private Boolean hasVoting = false;

    @Builder.Default
    private Boolean hasFile = false;

    @OneToOne(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private Vote vote;


    // 게시글 수정 메서드
    public void update(String title, String content, Boolean isNotice) {
        if (title != null) {
            this.title = title;
        }
        if (content != null) {
            this.content = content;
        }
        if (isNotice != null) {
            this.isNotice = isNotice;
        }
    }

    // 공지 여부 설정
    public void setIsNotice(Boolean isNotice) {
        this.isNotice = isNotice;
    }

    // 투표 설정 (양방향 편의 메서드)
    public void setVote(Vote vote) {
        this.vote = vote;
        if (vote != null && vote.getPost() != this) {
            vote.setPost(this);
        }
        // hasVoting 자동 동기화
        this.hasVoting = (vote != null);
    }

    // 파일 여부 설정
    public void setHasFile(boolean hasAnyFile) {
        this.hasFile = hasAnyFile;
    }
}
