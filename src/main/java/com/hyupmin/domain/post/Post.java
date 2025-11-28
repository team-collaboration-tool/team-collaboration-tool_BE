package com.hyupmin.domain.post;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.hyupmin.domain.attachmentFile.AttachmentFile;
import com.hyupmin.domain.project.Project;
import com.hyupmin.domain.user.User;
import com.hyupmin.domain.BaseTimeEntity; //게시글 생성 및 수정 관련
import java.time.LocalDateTime;
import java.util.List;
import com.hyupmin.domain.vote.Vote;

@Entity
@Table(name = "posts")
@Getter
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

    //삭제
    //private LocalDateTime createdAt;
    //private LocalDateTime updatedAt;

    private Boolean isNotice = false;
    private Boolean hasVoting = false;
    private Boolean hasFile = false;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<AttachmentFile> attachments;

    @OneToOne(mappedBy = "post", cascade = CascadeType.ALL)
    private Vote vote;

    //프로젝트 수정 메서드
    public void update(String title, String content, Boolean isNotice) {
        if (title != null) {
            this.title = title;
        }
        if (content != null) {
            this.content = content;
        }
        // 공지사항 여부 업데이트 (null이 아닐 때만 변경)
        if (isNotice != null) {
            this.isNotice = isNotice;
        }
        // createdAt, updatedAt은 BaseTimeEntity가 알아서 처리하거나
        // this.updatedAt = LocalDateTime.now(); 로직 유지
    }
    //공지사항 여부
    public void setIsNotice(Boolean isNotice) {
        this.isNotice = isNotice;
    }

    public void setVote(Vote vote) {
        this.vote = vote;
        vote.setPost(this); // 양방향 편의 메서드
    }

}