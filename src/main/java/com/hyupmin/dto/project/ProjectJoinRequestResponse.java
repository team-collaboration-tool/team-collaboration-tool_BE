package com.hyupmin.dto.project;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProjectJoinRequestResponse { // 사용자가 프로젝트 코드를 입력하거나, 방장이 참여 요청 목록을 조회할 때 사용
    private Long projectPk;
    private Long projectUserPk;
    private String projectName;
    private String requesterEmail;
    private String requesterName;
    private String status;
    private String message; // 참여 요청 시 사용자에게 보여줄 메시지
}