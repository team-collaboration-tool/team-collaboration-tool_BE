package com.hyupmin.domain;
//게시글 생성 및 수정 시간 자동화
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass // 1. 이 클래스를 상속하는 엔티티에 아래 필드들을 컬럼으로 추가
@EntityListeners(AuditingEntityListener.class) // 2. JPA Auditing 활성화
public abstract class BaseTimeEntity {

    @CreatedDate // 3. 엔티티 생성 시 자동으로 시간 저장
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate // 4. 엔티티 수정 시 자동으로 시간 갱신
    private LocalDateTime updatedAt;
}
