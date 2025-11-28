package com.hyupmin.repository.post;

import com.hyupmin.domain.post.Post;
import com.hyupmin.domain.project.Project;
import org.springframework.data.domain.Page; // import
import org.springframework.data.domain.Pageable; // import
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // import
import org.springframework.data.repository.query.Param; // import

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    // JpaRepository<엔티티_이름, PK_타입>
    /**
     * 특정 프로젝트의 게시글 목록을 페이징하여 조회합니다.
     * * N+1 문제를 해결하기 위해 'JOIN FETCH p.user'를 사용,
     * 게시글을 가져올 때 작성자(User) 정보도 함께(즉시) 가져옵니다.
     *
     * @Query : JPA가 자동으로 쿼리를 만들지 않고, 우리가 직접 JPQL 쿼리를 작성합니다.
     * @Param : 메서드의 파라미터(project)를 쿼리(:project)에 바인딩합니다.
     */
    @Query(value = "SELECT p FROM Post p JOIN FETCH p.user WHERE p.project = :project",
            countQuery = "SELECT COUNT(p) FROM Post p WHERE p.project = :project")
    Page<Post> findByProjectWithUser(@Param("project") Project project, Pageable pageable);

    /**
     * 특정 게시글을 조회합니다. (상세 조회용)
     * * N+1 문제를 해결하기 위해 'JOIN FETCH'를 사용하여
     * 연관된 User와 Project 정보를 한 번의 쿼리로 함께 가져옵니다.
     */
    @Query("SELECT p FROM Post p " +
            "JOIN FETCH p.user " +
            "JOIN FETCH p.project " +
            "WHERE p.postPk = :postId")
    Optional<Post> findPostWithUserAndProjectById(@Param("postId") Long postId);

    /*
    * 공지사항 목록 조회
    * */
    @Query("SELECT p FROM Post p " +
            "JOIN FETCH p.user u " +
            "WHERE p.project = :project " +
            "AND p.isNotice = true " +
            "ORDER BY p.createdAt DESC")
    List<Post> findNoticePostsByProject(@Param("project") Project project);
}

