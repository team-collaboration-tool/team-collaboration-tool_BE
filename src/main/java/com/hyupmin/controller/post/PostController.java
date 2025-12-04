package com.hyupmin.controller.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyupmin.domain.attachmentFile.AttachmentFile;
import com.hyupmin.dto.post.PostCreateRequest;
import com.hyupmin.dto.post.PostResponse;
import com.hyupmin.dto.post.PostUpdateRequest;
import com.hyupmin.dto.post.PostSearchType;
import com.hyupmin.repository.attachmentFile.AttachmentFileRepository;
import com.hyupmin.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;
    private final AttachmentFileRepository attachmentFileRepository;

    /**
     * 게시글 생성 API
     * [POST] /api/posts
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostResponse> createPost(
            @AuthenticationPrincipal String userEmail,
            @RequestPart("post") String postJson,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        PostCreateRequest request = objectMapper.readValue(postJson, PostCreateRequest.class);

        PostResponse response = postService.createPost(request, files, userEmail);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 특정 게시글 조회 API
     * [GET] /api/posts/{postId}
     */
    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPostById(
            @PathVariable Long postId,
            @AuthenticationPrincipal String userEmail
    ) {

        PostResponse response = postService.getPostById(postId, userEmail);
        return ResponseEntity.ok(response);
    }

    /**
     * 특정 게시글 수정 API
     * [PUT] /api/posts/{postId}
     */
    @PutMapping(value = "/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostResponse> updatePost(
            @AuthenticationPrincipal String userEmail,
            @PathVariable Long postId,
            @RequestPart("post") PostUpdateRequest request,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) throws IOException {

        PostResponse response = postService.updatePost(postId, request, files, userEmail);
        return ResponseEntity.ok(response);
    }

    /**
     * 특정 게시글 삭제 API
     * [DELETE] /api/posts/{postId}
     */
    @DeleteMapping("/{postId}")
    public ResponseEntity<Map<String, Object>> deletePost(
            @AuthenticationPrincipal String userEmail,
            @PathVariable Long postId) {

        postService.deletePost(postId, userEmail);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "게시글이 성공적으로 삭제되었습니다.");
        response.put("postId", postId);

        return ResponseEntity.ok(response);
    }

    /**
     * 게시글 목록 조회 API (페이징)
     * [GET] /api/posts
     */
    @GetMapping
    public ResponseEntity<Page<PostResponse>> getAllPosts(
            @RequestParam Long projectPk,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "ALL") PostSearchType searchType,
            @PageableDefault(size = 10, sort = {"isNotice", "createdAt"}, direction = Sort.Direction.DESC)
            Pageable pageable) {

        Page<PostResponse> responsePage =
                postService.getPostsByProject(projectPk, keyword, searchType, pageable);

        return ResponseEntity.ok(responsePage);
    }

    /**
     * 게시글 공지사항 등록 API
     * [PATCH] /api/posts/{postId}/notice
     */
    @PatchMapping("/{postId}/notice")
    public ResponseEntity<PostResponse> markAsNotice(
            @AuthenticationPrincipal String userEmail,
            @PathVariable Long postId) {

        PostResponse response = postService.markAsNotice(postId, userEmail);
        return ResponseEntity.ok(response);
    }

    /**
     * 게시글 공지사항 해제 API
     * [PATCH] /api/posts/{postId}/notice/cancel
     */
    @PatchMapping("/{postId}/notice/cancel")
    public ResponseEntity<PostResponse> unmarkAsNotice(
            @AuthenticationPrincipal String userEmail,
            @PathVariable Long postId) {

        PostResponse response = postService.unmarkAsNotice(postId, userEmail);
        return ResponseEntity.ok(response);
    }

    /**
     * 공지사항 목록 조회 API
     * [GET] /api/posts/notices
     */
    @GetMapping("/notices")
    public ResponseEntity<List<PostResponse>> getNoticePosts(
            @RequestParam Long projectPk) {

        List<PostResponse> response = postService.getNoticePostsByProject(projectPk);
        return ResponseEntity.ok(response);
    }

    /**
     * 특정 게시글의 첨부파일 다운로드
     * [GET] /api/posts/{postId}/attachments/{attachmentId}/download
     */
    @GetMapping("/{postId}/attachments/{attachmentId}/download")
    public ResponseEntity<Resource> downloadAttachment(
            @PathVariable Long postId,
            @PathVariable Long attachmentId
    ) throws MalformedURLException, UnsupportedEncodingException {

        AttachmentFile attachment = attachmentFileRepository
                .findByAttachmentPkAndIsDeletedFalse(attachmentId)
                .orElseThrow(() -> new IllegalArgumentException("첨부파일을 찾을 수 없습니다."));

        if (!attachment.getPost().getPostPk().equals(postId)) {
            throw new IllegalArgumentException("해당 게시글의 첨부파일이 아닙니다.");
        }

        Path filePath = Paths.get(attachment.getFilePath());

        Resource resource = new UrlResource(filePath.toUri());
        if (!resource.exists() || !resource.isReadable()) {
            throw new IllegalStateException("파일을 읽을 수 없습니다.");
        }

        String contentType;
        try {
            contentType = Files.probeContentType(filePath);
        } catch (Exception e) {
            contentType = null;
        }
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        String encodedFileName = URLEncoder.encode(attachment.getOriginalFileName(), "UTF-8")
                .replaceAll("\\+", "%20");

        String contentDisposition = "attachment; filename=\"" + encodedFileName + "\"";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(resource);
    }
}
