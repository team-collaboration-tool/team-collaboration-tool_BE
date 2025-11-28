package com.hyupmin.repository.notice;

import com.hyupmin.domain.notice.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    // 프로젝트의 공지사항 목록 조회 (최신순)
    List<Notice> findByProject_ProjectPkOrderByCreatedAtDesc(Long projectPk);
}