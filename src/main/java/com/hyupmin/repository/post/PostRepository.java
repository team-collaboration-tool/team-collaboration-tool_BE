package com.hyupmin.repository.post;

import com.hyupmin.domain.post.Post;
import com.hyupmin.domain.project.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    /**
     * 특정 프로젝트의 게시글 목록을 페이징하여 조회합니다.
     * - 작성자(User) 정보를 함께 조회하기 위해 JOIN FETCH 사용
     * - 공지글(isNotice = true)을 먼저, 이후 postNumber 내림차순 정렬
     */
    @Query(value = "SELECT p FROM Post p " +
            "JOIN FETCH p.user " +
            "WHERE p.project = :project " +
            "ORDER BY p.isNotice DESC, p.postNumber DESC",
            countQuery = "SELECT COUNT(p) FROM Post p WHERE p.project = :project")
    Page<Post> findByProjectWithUser(@Param("project") Project project, Pageable pageable);

    /**
     * 특정 게시글 상세 조회 (작성자, 프로젝트 함께 조회)
     */
    @Query("SELECT p FROM Post p " +
            "JOIN FETCH p.user " +
            "JOIN FETCH p.project " +
            "WHERE p.postPk = :postId")
    Optional<Post> findPostWithUserAndProjectById(@Param("postId") Long postId);

    /**
     * 공지사항 목록 조회
     * - 공지글만 필터링
     * - postNumber 내림차순 정렬
     */
    @Query("SELECT p FROM Post p " +
            "JOIN FETCH p.user u " +
            "WHERE p.project = :project " +
            "AND p.isNotice = true " +
            "ORDER BY p.postNumber DESC")
    List<Post> findNoticePostsByProject(@Param("project") Project project);

    /**
     * 프로젝트별 현재 가장 큰 postNumber 조회 (없으면 0)
     */
    @Query("SELECT COALESCE(MAX(p.postNumber), 0) FROM Post p WHERE p.project = :project")
    Long findMaxPostNumberByProject(@Param("project") Project project);

    /**
     * 특정 게시글이 삭제되었을 때, 그 뒤 번호(postNumber > X)를 가진 게시글들 조회
     * - 삭제된 번호 뒤에 있는 글들의 번호를 -1 하기 위해 사용
     */
    List<Post> findByProjectAndPostNumberGreaterThanOrderByPostNumberAsc(Project project, Long postNumber);


    // 1) 제목으로 검색 (postNumber 기준 정렬)
    @Query(value = "SELECT p FROM Post p " +
            "JOIN FETCH p.user u " +
            "WHERE p.project = :project " +
            "AND LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "ORDER BY p.postNumber DESC",
            countQuery = "SELECT COUNT(p) FROM Post p " +
                    "WHERE p.project = :project " +
                    "AND LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Post> searchByProjectAndTitle(@Param("project") Project project,
                                       @Param("keyword") String keyword,
                                       Pageable pageable);

    // 2) 작성자 이름으로 검색 (postNumber 기준 정렬)
    @Query(value = "SELECT p FROM Post p " +
            "JOIN FETCH p.user u " +
            "WHERE p.project = :project " +
            "AND LOWER(u.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "ORDER BY p.postNumber DESC",
            countQuery = "SELECT COUNT(p) FROM Post p " +
                    "JOIN p.user u " +
                    "WHERE p.project = :project " +
                    "AND LOWER(u.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Post> searchByProjectAndAuthor(@Param("project") Project project,
                                        @Param("keyword") String keyword,
                                        Pageable pageable);

    // 3) 제목 OR 작성자 통합 검색 (postNumber 기준 정렬)
    @Query(value = "SELECT p FROM Post p " +
            "JOIN FETCH p.user u " +
            "WHERE p.project = :project " +
            "AND (LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "     OR LOWER(u.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "ORDER BY p.postNumber DESC",
            countQuery = "SELECT COUNT(p) FROM Post p " +
                    "JOIN p.user u " +
                    "WHERE p.project = :project " +
                    "AND (LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
                    "     OR LOWER(u.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) ")
    Page<Post> searchByProjectAndTitleOrAuthor(@Param("project") Project project,
                                               @Param("keyword") String keyword,
                                               Pageable pageable);

}