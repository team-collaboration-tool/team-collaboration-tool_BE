package com.hyupmin.dto.project;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor // JSON 역직렬화를 위해 필요
@AllArgsConstructor // Builder 패턴을 사용하지 않는 경우를 위해 추가
public class ProjectLeaveRequest {
    private String projectName; // UI에서 사용자가 입력하는 프로젝트명
}