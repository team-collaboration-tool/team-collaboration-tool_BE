package com.hyupmin.domain.attachmentFile;

import jakarta.persistence.*;
import lombok.*;
import com.hyupmin.domain.post.Post;

import java.time.LocalDateTime;

@Entity
@Table(name = "attachments_file")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttachmentFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long attachmentPk;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_pk", nullable = false)
    private Post post;

    @Column(nullable = false)
    private String originalFileName;

    @Column(nullable = false)
    private String storedFileName;

    @Column(nullable = false)
    private String filePath;

    @Column(nullable = false)
    private Long fileSize;

    @Column(nullable = false)
    private String fileType;

    @Column(nullable = false)
    private boolean isDeleted;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}