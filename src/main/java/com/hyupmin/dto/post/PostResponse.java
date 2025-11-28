package com.hyupmin.dto.post;

import lombok.Getter;
import com.hyupmin.domain.post.Post;
//import com.hyupmin.dto.voteResponse.VoteResponse;

import java.time.LocalDateTime;
import java.util.List;
// import java.util.stream.Collectors; // (주석 처리된 곳에서만 쓰므로 일단 삭제)

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

    //private VoteResponse vote;
    private List<Long> attachmentIds;

    // *** 엔티티(Post)를 DTO(PostResponse)로 변환하는 생성자 ***
    public PostResponse(Post post) {
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

        // (주석 처리된 투표/첨부파일 로직)
        // ...
    }
}