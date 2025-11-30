package com.hyupmin.domain.attachmentFile;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.hyupmin.domain.post.Post;

@Entity
@Table(name = "attachments_file")
@Getter
@NoArgsConstructor
public class AttachmentFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long attachmentPk;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_pk", nullable = false)
    private Post post;

    @Column(nullable = false)
    private String fileName;
}