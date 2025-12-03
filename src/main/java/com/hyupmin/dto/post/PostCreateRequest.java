package com.hyupmin.dto.post;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor // 스프링이 JSON을 자바 객체로 변환할 때 기본 생성자가 필요합니다.
public class PostCreateRequest {

    // 1. 어느 프로젝트에 속한 게시글인지
    //    (Post 엔티티의 project 필드와 연결)
    private Long projectPk;

    // 2. 게시글 자체의 정보
    private String title;
    private String content;

    // 3. 옵션 정보
    private Boolean isNotice;  // 공지사항 여부
    private Boolean hasVoting; // 투표 포함 여부

    // 4. 투표 정보 (만약 hasVoting == true 라면)
    private String voteTitle;         // 투표 제목
    private List<String> voteOptions; // 투표 항목 리스트 (예: ["찬성", "반대"])

    // true  -> 중복 선택 가능
    // false -> 단일 선택만 가능
    private Boolean allowMultipleChoices;

    // true  -> 익명 투표
    // false -> 실명 투표
    private Boolean isAnonymous;
}