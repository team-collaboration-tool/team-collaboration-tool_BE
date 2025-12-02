package com.hyupmin.domain.project;

import jakarta.persistence.*;
import lombok.*;
import com.hyupmin.domain.calendar.CalendarEvent;
import com.hyupmin.domain.post.Post;
import com.hyupmin.domain.projectUser.ProjectUser;
import com.hyupmin.domain.timepoll.TimePoll;
import com.hyupmin.domain.user.User;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "projects") // 테이블명 수정
@NoArgsConstructor
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long projectPk;

    @Column(nullable = false)
    private String name; // DBML: project_name

    @Column(nullable = false, unique = true)
    private String joinCode; // DBML: invite_code

    // DBML: project_owner_user_pk (프로젝트 소유자)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_owner_user_pk", nullable = false) // JoinColumn 이름 수정
    private User owner;

    // == 연관 관계 ==

    // 1. 프로젝트 멤버 목록 (ProjectUser) 프로젝트와 사용자를 연결하는 중간테이블(projectUser)과 관계 설정.
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectUser> projectUsers = new ArrayList<>();

    // 2. 프로젝트 일정 (Calendar Event)
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CalendarEvent> calendarEvents = new ArrayList<>();

    // 3. 프로젝트 게시글 (Post)
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    // 4. 프로젝트 시간 조율표 (Time Poll)
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TimePoll> timePolls = new ArrayList<>();


    // 생성자
    public Project(String name, User owner) {
        this.name = name;
        this.owner = owner;
        // 프로젝트 생성 시 고유한 참여 코드 자동 생성
        this.joinCode = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    // == 비즈니스 로직 ==
    // 프로젝트 정보 수정
    public void update(String name) {
        if (name != null) this.name = name;
    }

    public void changeOwner(User newOwner) {
        this.owner = newOwner;
    }

}