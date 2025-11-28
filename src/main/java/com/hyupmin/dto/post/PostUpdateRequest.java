package com.hyupmin.dto.post;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostUpdateRequest {
    private String title;
    private String content;
    private Boolean isNotice;
    // (이 외에 수정이 필요한 필드가 있다면 추가)
}