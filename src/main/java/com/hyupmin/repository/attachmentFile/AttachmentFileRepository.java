package com.hyupmin.repository.attachmentFile;

import com.hyupmin.domain.attachmentFile.AttachmentFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AttachmentFileRepository extends JpaRepository<AttachmentFile, Long> {

    /**
     * 특정 게시글에 속한 첨부파일들 (삭제 안 된 것만)
     */
    List<AttachmentFile> findByPost_PostPkAndIsDeletedFalse(Long postPk);

    /**
     * 단일 첨부파일 조회 (삭제 안 된 것만)
     */
    Optional<AttachmentFile> findByAttachmentPkAndIsDeletedFalse(Long attachmentPk);

    /**
     * 게시글에 속한 첨부파일들의 총 용량 (논리삭제되지 않은 것만)
     * - 게시글당 총 용량 제한 체크할 때 사용
     */
    @Query("SELECT COALESCE(SUM(a.fileSize), 0) " +
            "FROM AttachmentFile a " +
            "WHERE a.post.postPk = :postPk AND a.isDeleted = false")
    long sumFileSizeByPostPk(@Param("postPk") Long postPk);
}