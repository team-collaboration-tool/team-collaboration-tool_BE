package com.hyupmin.domain.post;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.hyupmin.domain.attachmentFile.AttachmentFile;
import com.hyupmin.domain.project.Project;
import com.hyupmin.domain.user.User;
import java.time.LocalDateTime;
import java.util.List;
import com.hyupmin.domain.vote.Vote;
@Entity
@Table(name = "posts")
@Getter
@NoArgsConstructor
public class Post {

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

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Boolean isNotice = false;
    private Boolean hasVoting = false;
    private Boolean hasFile = false;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<AttachmentFile> attachments;

    @OneToOne(mappedBy = "post", cascade = CascadeType.ALL)
    private Vote vote;
}