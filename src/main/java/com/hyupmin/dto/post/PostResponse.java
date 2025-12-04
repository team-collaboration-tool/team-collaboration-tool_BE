package com.hyupmin.dto.post;


import lombok.Getter;
import com.hyupmin.domain.post.Post;
import com.hyupmin.domain.attachmentFile.AttachmentFile;
import com.hyupmin.domain.vote.VoteRecord;
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

    private Boolean isAuthor;

    private VoteResponse vote;

    private List<Long> attachmentIds;

    private Long postNumber;


    public void setPostNumber(Long postNumber) {
        this.postNumber = postNumber;
    }

    public PostResponse(Post post) {
        this(post, null, null);
    }

    public PostResponse(Post post, Boolean hasVoted) {
        this(post, hasVoted, null);
    }

    public PostResponse(Post post, Boolean hasVoted, Boolean isAuthor) {
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

        this.isAuthor = isAuthor != null && isAuthor;

        if (Boolean.TRUE.equals(post.getHasVoting()) && post.getVote() != null) {
            boolean voted = hasVoted != null && hasVoted;
            this.vote = new VoteResponse(post.getVote(), voted);
        } else {
            this.vote = null;
        }

        if (post.getAttachmentFiles() != null) {
            this.attachmentIds = post.getAttachmentFiles().stream()
                    .filter(file -> !file.isDeleted())
                    .map(AttachmentFile::getAttachmentPk)
                    .toList();
        }

    }

    public PostResponse(Post post, Boolean hasVoted, Boolean isAuthor, List<VoteRecord> voteRecords) {

        this(post, hasVoted, isAuthor);

        if (Boolean.TRUE.equals(post.getHasVoting())
                && post.getVote() != null
                && Boolean.FALSE.equals(post.getVote().getIsAnonymous())
                && voteRecords != null
                && !voteRecords.isEmpty()) {

            boolean voted = hasVoted != null && hasVoted;
            this.vote = new VoteResponse(post.getVote(), voted, voteRecords);
        }
    }
}
