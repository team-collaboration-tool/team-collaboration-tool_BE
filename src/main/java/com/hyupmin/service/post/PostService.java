package com.hyupmin.service.post;

import lombok.RequiredArgsConstructor;
import com.hyupmin.domain.post.Post;
import com.hyupmin.domain.project.Project;
import com.hyupmin.domain.user.User;
import com.hyupmin.domain.vote.Vote;
import com.hyupmin.domain.vote.VoteOption;
import com.hyupmin.dto.post.PostCreateRequest;
import com.hyupmin.dto.post.PostResponse;
import com.hyupmin.dto.post.PostUpdateRequest;
import com.hyupmin.repository.post.PostRepository;
import com.hyupmin.repository.project.ProjectRepository;
import com.hyupmin.repository.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;

import com.hyupmin.file.FileStore;
import com.hyupmin.domain.attachmentFile.AttachmentFile;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final FileStore fileStore;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    /**
     * 게시글 생성
     */
    @Transactional
    public PostResponse createPost(PostCreateRequest request,
                                   List<MultipartFile> files,
                                   String userEmail) throws IOException {

        // 1. 작성자 조회
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 2. 프로젝트 조회
        Project project = projectRepository.findById(request.getProjectPk())
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다."));

        // (첨부파일 저장 로직은 나중에)
        // List<AttachmentFile> attachmentFiles = fileStore.storeFiles(files);

        // 3. Post 엔티티 생성
        Post newPost = Post.builder()
                .project(project)
                .user(user)
                .title(request.getTitle())
                .content(request.getContent())
                .isNotice(request.getIsNotice())
                .hasVoting(request.getHasVoting())
                // .hasFile(!attachmentFiles.isEmpty())
                .build();

        // 첨부파일 연관관계 설정은 일단 주석
        /*
        for (AttachmentFile file : attachmentFiles) {
            file.setPost(newPost);
        }
        */

        // 4. 투표 생성 로직
        if (request.getHasVoting() != null && request.getHasVoting()) {
            if (request.getVoteTitle() == null
                    || request.getVoteOptions() == null
                    || request.getVoteOptions().isEmpty()) {
                throw new IllegalArgumentException("투표 제목과 항목은 필수입니다.");
            }

            Vote vote = Vote.builder()
                    .title(request.getVoteTitle())
                    .post(newPost)
                    .build();

            for (String optionText : request.getVoteOptions()) {
                VoteOption option = VoteOption.builder()
                        .content(optionText)
                        .vote(vote)
                        .build();
                vote.addOption(option);
            }

            newPost.setVote(vote);
        }

        // 5. 저장
        Post savedPost = postRepository.save(newPost);

        // 6. DTO 반환
        return new PostResponse(savedPost);
    }

    /**
     * 특정 게시글 조회
     */
    public PostResponse getPostById(Long postId) {
        Post post = postRepository.findPostWithUserAndProjectById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        return new PostResponse(post);
    }

    /**
     * 특정 프로젝트의 게시글 목록 조회 (페이징 적용)
     */
    public Page<PostResponse> getPostsByProject(Long projectPk, Pageable pageable) {
        Project project = projectRepository.findById(projectPk)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다."));

        Page<Post> postsPage = postRepository.findByProjectWithUser(project, pageable);
        return postsPage.map(PostResponse::new);
    }

    /**
     * 게시글 수정
     */
    @Transactional
    public PostResponse updatePost(Long postId, PostUpdateRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Post post = postRepository.findPostWithUserAndProjectById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        if (!post.getUser().equals(user)) {
            throw new SecurityException("수정 권한이 없습니다.");
        }

        post.update(request.getTitle(), request.getContent(), request.getIsNotice());
        return new PostResponse(post);
    }

    /**
     * 게시글 삭제
     */
    @Transactional
    public void deletePost(Long postId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Post post = postRepository.findPostWithUserAndProjectById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        if (!post.getUser().equals(user)) {
            throw new SecurityException("삭제 권한이 없습니다.");
        }

        postRepository.delete(post);
    }

    /**
     * 게시글을 공지사항으로 등록
     */
    @Transactional
    public PostResponse markAsNotice(Long postId, String userEmail) {

        // 1. 사용자 조회
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 2. 게시글 조회
        Post post = postRepository.findPostWithUserAndProjectById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        // 3. 권한 체크 (작성자만 가능)
        if (!post.getUser().equals(user)) {
            throw new SecurityException("공지 등록 권한이 없습니다.");
        }

        // 4. 공지사항으로 변경
        post.setIsNotice(true);

        // 5. 응답 반환
        return new PostResponse(post);
    }

    /**
     * 게시글 공지사항에서 해제
     */
    @Transactional
    public PostResponse unmarkAsNotice(Long postId, String userEmail) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Post post = postRepository.findPostWithUserAndProjectById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        if (!post.getUser().equals(user)) {
            throw new SecurityException("공지 해제 권한이 없습니다.");
        }

        post.setIsNotice(false);

        return new PostResponse(post);
    }

    /**
     * 특정 프로젝트의 공지사항 목록 조회
     */
    public List<PostResponse> getNoticePostsByProject(Long projectPk) {

        // 1. 프로젝트 조회
        Project project = projectRepository.findById(projectPk)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다."));

        // 2. 공지글 목록 조회
        List<Post> noticePosts = postRepository.findNoticePostsByProject(project);

        // 3. PostResponse 리스트로 변환
        return noticePosts.stream()
                .map(PostResponse::new)
                .toList();
    }

}
