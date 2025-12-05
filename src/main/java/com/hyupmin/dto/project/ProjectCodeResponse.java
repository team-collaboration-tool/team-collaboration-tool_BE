package com.hyupmin.dto.project;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProjectCodeResponse {
    private Long projectPk;
    private String projectName; // 참여코드 빼고 다 없어도 될 것 같기도
    private String joinCode; // 실제 참여 코드
}