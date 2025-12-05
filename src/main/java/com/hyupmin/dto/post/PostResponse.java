package com.hyupmin.dto.post;

import lombok.Getter;
import com.hyupmin.domain.post.Post;
import com.hyupmin.domain.attachmentFile.AttachmentFile;
import com.hyupmin.dto.vote.VoteResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class PostResponse {

    private Long postPk;
    private Long projectPk;
    private String authorName;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isNotice;
    private Boolean hasVoting;
    private Boolean hasFile;

    // 투표 정보 (게시글에 투표가 있을 경우에만 포함)
    private VoteResponse vote;

    // 첨부파일 ID 목록
    private List<Long> attachmentIds;

    public PostResponse(Post post) {
        this(post, null);   // 다른 생성자 내부 호출
    }

    // *** 엔티티(Post)를 DTO(PostResponse)로 변환하는 생성자 ***
    public PostResponse(Post post, Boolean hasVoted) {
        this.postPk = post.getPostPk();
        this.projectPk = post.getProject().getProjectPk();
        this.authorName = post.getUser().getName();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.createdAt = post.getCreatedAt();
        this.updatedAt = post.getUpdatedAt();
        this.isNotice = post.getIsNotice();
        this.hasVoting = post.getHasVoting();
        this.hasFile = post.getHasFile();

        // 투표 정보 매핑 (hasVoting == true 이고 실제 Vote가 있을 때만)
        if (Boolean.TRUE.equals(post.getHasVoting()) && post.getVote() != null) {
            boolean voted = hasVoted != null && hasVoted; // null이면 false
            this.vote = new VoteResponse(post.getVote(), voted);
        } else {
            this.vote = null;
        }

        // 첨부파일 ID 목록 매핑
        if (post.getAttachmentFiles() != null) {
            this.attachmentIds = post.getAttachmentFiles().stream()
                    .filter(file -> !file.isDeleted())
                    .map(AttachmentFile::getAttachmentPk)
                    .toList();
        }

    }
}
