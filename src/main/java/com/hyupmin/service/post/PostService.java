package com.hyupmin.service.post;

import com.hyupmin.domain.vote.VoteRecord;
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
import com.hyupmin.dto.post.PostSearchType;
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
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import com.hyupmin.file.FileStore;
import com.hyupmin.domain.attachmentFile.AttachmentFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.PageImpl;

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

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Project project = projectRepository.findById(request.getProjectPk())
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다."));

        List<AttachmentFile> attachmentFiles = fileStore.storeFiles(files);

        Long maxPostNumber = postRepository.findMaxPostNumberByProject(project);
        Long nextPostNumber = maxPostNumber + 1;

        Post newPost = Post.builder()
                .project(project)
                .user(user)
                .postNumber(nextPostNumber)
                .title(request.getTitle())
                .content(request.getContent())
                .isNotice(Boolean.TRUE.equals(request.getIsNotice()))
                .hasVoting(Boolean.TRUE.equals(request.getHasVoting()))
                .hasFile(attachmentFiles != null && !attachmentFiles.isEmpty())
                .build();

        if (attachmentFiles != null) {
            for (AttachmentFile file : attachmentFiles) {
                file.setPost(newPost);
            }
        }

        if (Boolean.TRUE.equals(request.getHasVoting())) {
            if (request.getVoteTitle() == null
                    || request.getVoteOptions() == null
                    || request.getVoteOptions().isEmpty()) {
                throw new IllegalArgumentException("투표 제목과 항목은 필수입니다.");
            }

            boolean allowMultiple = Boolean.TRUE.equals(request.getAllowMultipleChoices());
            boolean anonymous = Boolean.TRUE.equals(request.getIsAnonymous());


            LocalDateTime endTime = null;
            if (request.getVoteEndTime() != null && !request.getVoteEndTime().isBlank()) {
                try {
                    endTime = LocalDateTime.parse(
                            request.getVoteEndTime(),
                            DateTimeFormatter.ISO_LOCAL_DATE_TIME // "yyyy-MM-dd'T'HH:mm:ss"
                    );
                } catch (DateTimeParseException e) {
                    throw new IllegalArgumentException("투표 마감 시간 형식이 올바르지 않습니다. 예) 2025-12-31T23:59:00");
                }
            }

            Vote vote = Vote.builder()
                    .post(newPost)
                    .title(request.getVoteTitle())
                    .startTime(LocalDateTime.now())
                    .endTime(endTime)
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

            newPost.setVote(vote);
        }

        Post savedPost = postRepository.save(newPost);

        if (attachmentFiles != null) {
            for (AttachmentFile file : attachmentFiles) {
                attachmentFileRepository.save(file);
            }
        }

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

        List<VoteRecord> voteRecords = null;

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

        if (Boolean.TRUE.equals(post.getHasVoting())
                && post.getVote() != null
                && Boolean.FALSE.equals(post.getVote().getIsAnonymous())) {

            voteRecords = voteRecordRepository.findByVoteOption_Vote(post.getVote());
        }


        return new PostResponse(post, hasVoted, isAuthor, voteRecords);

    }

    /**
     * 특정 프로젝트의 게시글 목록 조회 (페이징 + 검색)
     */
    public Page<PostResponse> getPostsByProject(Long projectPk,
                                                String keyword,
                                                PostSearchType searchType,
                                                Pageable pageable) {

        Project project = projectRepository.findById(projectPk)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다."));

        Page<Post> postsPage;

        if (keyword == null || keyword.isBlank()) {
            postsPage = postRepository.findByProjectWithUser(project, pageable);
        } else {

            if (searchType == null) {
                searchType = PostSearchType.ALL;
            }

            switch (searchType) {
                case TITLE:
                    postsPage = postRepository.searchByProjectAndTitle(project, keyword, pageable);
                    break;

                case AUTHOR:
                    postsPage = postRepository.searchByProjectAndAuthor(project, keyword, pageable);
                    break;

                case ALL:
                default:
                    postsPage = postRepository.searchByProjectAndTitleOrAuthor(project, keyword, pageable);
                    break;
            }
        }

        List<PostResponse> dtoList = new ArrayList<>();

        for (Post post : postsPage.getContent()) {
            PostResponse dto = new PostResponse(post);
            dto.setPostNumber(post.getPostNumber());
            dtoList.add(dto);
        }

        return new PageImpl<>(dtoList, postsPage.getPageable(), postsPage.getTotalElements());
    }


    /**
     * 게시글 수정
     */
    @Transactional
    public PostResponse updatePost(Long postId,
                                   PostUpdateRequest request,
                                   List<MultipartFile> files,
                                   String userEmail) throws IOException {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Post post = postRepository.findPostWithUserAndProjectById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        if (!post.getUser().equals(user)) {
            throw new SecurityException("수정 권한이 없습니다.");
        }

        post.update(request.getTitle(), request.getContent(), request.getIsNotice());

        List<AttachmentFile> existingFiles =
                attachmentFileRepository.findByPost_PostPkAndIsDeletedFalse(postId);

        List<AttachmentFile> newFiles = fileStore.storeFiles(files);
        if (newFiles != null) {
            for (AttachmentFile file : newFiles) {
                file.setPost(post);
                attachmentFileRepository.save(file);
            }
        }

        boolean hasAnyFile =
                (existingFiles != null && !existingFiles.isEmpty())
                        || (newFiles != null && !newFiles.isEmpty());
        post.setHasFile(hasAnyFile);

        if (request.getHasVoting() != null) {

            if (!request.getHasVoting()) {

                if (post.getVote() != null) {
                    Vote existingVote = post.getVote();
                    post.setVote(null);
                    voteRepository.delete(existingVote);
                }
                post.setHasVoting(false);

            } else {

                if (post.getVote() == null) {

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

        Project project = post.getProject();
        Long deletedPostNumber = post.getPostNumber();
        
        postRepository.delete(post);

        List<Post> postsToShift =
                postRepository.findByProjectAndPostNumberGreaterThanOrderByPostNumberAsc(project, deletedPostNumber);

        for (Post p : postsToShift) {
            p.setPostNumber(p.getPostNumber() - 1);
            // save 안 해도 @Transactional + 더티체킹으로 UPDATE 됨
        }
    }


    /**
     * 게시글을 공지사항으로 등록
     */
    @Transactional
    public PostResponse markAsNotice(Long postId, String userEmail) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Post post = postRepository.findPostWithUserAndProjectById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        if (!post.getUser().equals(user)) {
            throw new SecurityException("공지 등록 권한이 없습니다.");
        }

        post.setIsNotice(true);

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

        Project project = projectRepository.findById(projectPk)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다."));

        List<Post> noticePosts = postRepository.findNoticePostsByProject(project);

        return noticePosts.stream()
                .map(PostResponse::new)
                .toList();
    }

}
