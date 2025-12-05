package com.hyupmin.domain.notice;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.hyupmin.domain.project.Project;
import com.hyupmin.domain.user.User;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "notices")
@NoArgsConstructor
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long noticePk;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_pk", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "create_user_pk", nullable = false)
    private User createUser;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 생성자 (간단한 공지)
    public Notice(Project project, User createUser, String title, String content) {
        this.project = project;
        this.createUser = createUser;
        this.title = title;
        this.content = content;
        this.createdAt = LocalDateTime.now();
    }
}