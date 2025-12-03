package com.hyupmin.service.post;

import com.hyupmin.repository.vote.VoteRepository;
import com.hyupmin.repository.vote.VoteRecordRepository;
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
import com.hyupmin.repository.attachmentFile.AttachmentFileRepository;

import java.io.IOException;
import java.time.LocalDateTime;
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
    private final AttachmentFileRepository attachmentFileRepository;

    private final VoteRepository voteRepository;
    private final VoteRecordRepository voteRecordRepository;

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

        // 3. 파일 저장
        List<AttachmentFile> attachmentFiles = fileStore.storeFiles(files);

        // 4. Post 엔티티 생성
        Post newPost = Post.builder()
                .project(project)
                .user(user)
                .title(request.getTitle())
                .content(request.getContent())
                .isNotice(Boolean.TRUE.equals(request.getIsNotice()))
                .hasVoting(Boolean.TRUE.equals(request.getHasVoting()))
                .hasFile(attachmentFiles != null && !attachmentFiles.isEmpty())
                .build();

        // 첨부파일 연관관계
        if (attachmentFiles != null) {
            for (AttachmentFile file : attachmentFiles) {
                file.setPost(newPost);
            }
        }

        // 5. 투표 생성 로직
        if (Boolean.TRUE.equals(request.getHasVoting())) {
            if (request.getVoteTitle() == null
                    || request.getVoteOptions() == null
                    || request.getVoteOptions().isEmpty()) {
                throw new IllegalArgumentException("투표 제목과 항목은 필수입니다.");
            }

            boolean allowMultiple = Boolean.TRUE.equals(request.getAllowMultipleChoices());
            boolean anonymous = Boolean.TRUE.equals(request.getIsAnonymous());

            Vote vote = Vote.builder()
                    .post(newPost)
                    .title(request.getVoteTitle())
                    .startTime(LocalDateTime.now())
                    .endTime(null) // 필요하면 PostCreateRequest에 endTime 추가해서 받기
                    .allowMultipleChoices(allowMultiple)
                    .isAnonymous(anonymous)
                    .build();

            for (String optionText : request.getVoteOptions()) {
                if (optionText == null || optionText.isBlank()) continue;
                VoteOption option = VoteOption.builder()
                        .content(optionText)
                        .count(0)
                        .build();
                vote.addOption(option);
            }

            newPost.setVote(vote); // hasVoting도 true로 맞춰짐
        }

        // 6. 저장
        Post savedPost = postRepository.save(newPost);

        // 첨부파일 DB 저장
        if (attachmentFiles != null) {
            for (AttachmentFile file : attachmentFiles) {
                attachmentFileRepository.save(file);
            }
        }

        // 7. DTO 반환
        return new PostResponse(savedPost, false, true);
    }

    /**
     * 특정 게시글 조회
     *  - 현재 로그인 사용자가 게시글 내 투표를 했는지 여부까지 함께 반환
     */
    public PostResponse getPostById(Long postId, String userEmail) {

        Post post = postRepository.findPostWithUserAndProjectById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        boolean hasVoted = false;
        boolean isAuthor = false;

        // 로그인한 경우에만 투표 여부 체크
        if (userEmail != null) {
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

            if (post.getUser() != null && post.getUser().getUserPk() != null) {
                isAuthor = post.getUser().getUserPk().equals(user.getUserPk());
            }

            if (Boolean.TRUE.equals(post.getHasVoting()) && post.getVote() != null) {
                hasVoted = voteRecordRepository
                        .existsByUserAndVoteOption_Vote(user, post.getVote());
            }
        }

        return new PostResponse(post, hasVoted, isAuthor);
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
    public PostResponse updatePost(Long postId,
                                   PostUpdateRequest request,
                                   List<MultipartFile> files,
                                   String userEmail) throws IOException {

        // 1. 사용자 조회
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 2. 게시글 조회
        Post post = postRepository.findPostWithUserAndProjectById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        // 3. 권한 체크
        if (!post.getUser().equals(user)) {
            throw new SecurityException("수정 권한이 없습니다.");
        }

        // 4. 게시글 기본 정보 수정
        post.update(request.getTitle(), request.getContent(), request.getIsNotice());

        // 5. 기존 첨부파일 조회
        List<AttachmentFile> existingFiles =
                attachmentFileRepository.findByPost_PostPkAndIsDeletedFalse(postId);

        // 6. 새 파일 저장
        List<AttachmentFile> newFiles = fileStore.storeFiles(files);
        if (newFiles != null) {
            for (AttachmentFile file : newFiles) {
                file.setPost(post);
                attachmentFileRepository.save(file);
            }
        }

        // 7. hasFile 재계산
        boolean hasAnyFile =
                (existingFiles != null && !existingFiles.isEmpty())
                        || (newFiles != null && !newFiles.isEmpty());
        post.setHasFile(hasAnyFile);

        // 8. 투표 로직 (hasVoting가 null이면 건드리지 않음)
        if (request.getHasVoting() != null) {

            if (!request.getHasVoting()) {
                // 기존 투표가 있다면 삭제
                if (post.getVote() != null) {
                    Vote existingVote = post.getVote();
                    post.setVote(null);          // Post 기준 관계 해제
                    voteRepository.delete(existingVote); // 실제 DB 삭제
                }
                post.setHasVoting(false);

            } else {
                // 투표 켜기 또는 수정하기
                if (post.getVote() == null) {
                    // 1) 기존에 투표 없던 글에 새로 생성
                    if (request.getVoteTitle() == null
                            || request.getVoteOptions() == null
                            || request.getVoteOptions().isEmpty()) {
                        throw new IllegalArgumentException("투표 제목과 항목은 필수입니다.");
                    }

                    Vote vote = Vote.builder()
                            .post(post)
                            .title(request.getVoteTitle())
                            .startTime(LocalDateTime.now())
                            .endTime(request.getVoteEndTime())
                            .allowMultipleChoices(
                                    request.getAllowMultipleChoices() != null
                                            ? request.getAllowMultipleChoices()
                                            : false
                            )
                            .isAnonymous(
                                    request.getIsAnonymous() != null
                                            ? request.getIsAnonymous()
                                            : false
                            )
                            .build();

                    if (request.getVoteOptions() != null) {
                        for (String optionText : request.getVoteOptions()) {
                            if (optionText == null || optionText.isBlank()) continue;
                            VoteOption option = VoteOption.builder()
                                    .content(optionText)
                                    .count(0)
                                    .build();
                            vote.addOption(option);
                        }
                    }

                    post.setVote(vote);
                    post.setHasVoting(true);

                } else {
                    // 2) 기존 투표 수정
                    Vote vote = post.getVote();

                    if (request.getVoteTitle() != null) {
                        vote.setTitle(request.getVoteTitle());
                    }
                    if (request.getVoteEndTime() != null) {
                        vote.setEndTime(request.getVoteEndTime());
                    }
                    if (request.getAllowMultipleChoices() != null) {
                        vote.setAllowMultipleChoices(request.getAllowMultipleChoices());
                    }
                    if (request.getIsAnonymous() != null) {
                        vote.setIsAnonymous(request.getIsAnonymous());
                    }

                    if (request.getVoteOptions() != null) {
                        // 옵션 전체 갈아엎기
                        vote.clearOptions();
                        for (String optionText : request.getVoteOptions()) {
                            if (optionText == null || optionText.isBlank()) continue;
                            VoteOption option = VoteOption.builder()
                                    .content(optionText)
                                    .count(0)
                                    .build();
                            vote.addOption(option);
                        }
                    }

                    post.setHasVoting(true);
                }
            }
        }

        // 9. 응답 반환
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
