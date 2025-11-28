package com.hyupmin.controller.post;

import lombok.RequiredArgsConstructor;
import com.hyupmin.dto.post.PostCreateRequest;
import com.hyupmin.dto.post.PostResponse;
import com.hyupmin.dto.post.PostUpdateRequest;
import com.hyupmin.service.post.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

@RestController // 1. 이 클래스가 REST API 컨트롤러임을 선언
@RequiredArgsConstructor // 2. final 필드(PostService) 생성자 주입
@RequestMapping("/api/posts") // 3. 이 컨트롤러의 모든 API는 /api/posts 로 시작
public class PostController {

    private final PostService postService;

    /**
     * 게시글 생성 API
     * [POST] /api/posts
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostResponse> createPost(
            @RequestPart("post") String postJson,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) throws IOException {

        // JSON 문자열을 DTO로 직접 변환
        ObjectMapper objectMapper = new ObjectMapper();
        PostCreateRequest request = objectMapper.readValue(postJson, PostCreateRequest.class);

        String tempUserEmail = "testuser@example.com";

        PostResponse response = postService.createPost(request, files, tempUserEmail);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 특정 게시글 조회 API
     * [GET] /api/posts/{postId}
     */
    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable Long postId) {

        PostResponse response = postService.getPostById(postId);

        // 6. 200 OK 상태 코드와 함께 응답 본문을 반환
        return ResponseEntity.ok(response);
    }

    /**
     * 특정 게시글 수정 API
     * [PUT] /api/posts/{postId}
     */
    @PutMapping("/{postId}")
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable Long postId,
            @RequestBody PostUpdateRequest request) {

        // (TODO: 나중에 Spring Security로 로그인한 사용자 ID를 가져와서
        //  서비스의 권한 확인 로직에 넘겨줘야 함)
        String tempUserEmail = "testuser@example.com";
        PostResponse response = postService.updatePost(postId, request, tempUserEmail);

        // 200 OK 상태 코드와 함께 응답
        return ResponseEntity.ok(response);
    }

    /**
     * 특정 게시글 삭제 API
     * [DELETE] /api/posts/{postId}
     */
    @DeleteMapping("/{postId}")
    public ResponseEntity<Map<String, Object>> deletePost(@PathVariable Long postId) {

        String tempUserEmail = "testuser@example.com";
        postService.deletePost(postId, tempUserEmail);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "게시글이 성공적으로 삭제되었습니다.");
        response.put("postId", postId);

        return ResponseEntity.ok(response);
    }

    /**
     * 게시글 목록 조회 API (페이징)
     * [GET] /api/posts?projectPk=1&page=0&size=10&sort=createdAt,desc
     */
    @GetMapping
    public ResponseEntity<Page<PostResponse>> getAllPosts(

            // 1. 어떤 프로젝트의 게시글을 조회할지
            // (예: /api/posts?projectPk=1)
            @RequestParam Long projectPk,

            // 2. 페이징 및 정렬 정보
            @PageableDefault(size = 10, sort = {"isNotice", "createdAt"}, direction = Sort.Direction.DESC)
            Pageable pageable) {

        Page<PostResponse> responsePage = postService.getPostsByProject(projectPk, pageable);

        return ResponseEntity.ok(responsePage);
    }

    /*
    *  게시글 공지사항 등록 API
    * */

    @PatchMapping("/{postId}/notice")
    public ResponseEntity<PostResponse> markAsNotice(@PathVariable Long postId) {

        String tempUserEmail = "testuser@example.com";

        PostResponse response = postService.markAsNotice(postId, tempUserEmail);

        return ResponseEntity.ok(response);
    }

    /*
    * 게시글 공지사항 해제 API
    * */

    @PatchMapping("/{postId}/notice/cancel")
    public ResponseEntity<PostResponse> unmarkAsNotice(@PathVariable Long postId) {

        String tempUserEmail = "testuser@example.com";

        PostResponse response = postService.unmarkAsNotice(postId, tempUserEmail);

        return ResponseEntity.ok(response);
    }


    /**
     * 공지사항 목록 조회 API
     * [GET] /api/posts/notices?projectPk=1
     */
    @GetMapping("/notices")
    public ResponseEntity<List<PostResponse>> getNoticePosts(
            @RequestParam Long projectPk) {

        List<PostResponse> response = postService.getNoticePostsByProject(projectPk);

        return ResponseEntity.ok(response);
    }
}