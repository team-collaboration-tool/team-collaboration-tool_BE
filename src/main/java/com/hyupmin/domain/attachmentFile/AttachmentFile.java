package com.hyupmin.domain.attachmentFile;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.hyupmin.domain.post.Post;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_pk", nullable = false)
    private Post post;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String storeFileName; // 서버에 저장된 안 겹치는 파일명 (예: uuid-내사진.jpg)

    // 연관관계 편의 메서드
    public void setPost(Post post) {
        this.post = post;
    }
}