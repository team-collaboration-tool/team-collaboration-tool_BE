package com.hyupmin.domain.user;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

import com.hyupmin.domain.project.Project;
import com.hyupmin.domain.projectUser.ProjectUser;
import com.hyupmin.domain.shortcut.Shortcut;
import com.hyupmin.domain.post.Post;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userPk;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String phone;

    private String field;


    public User(String password, String name, String email, String phone, String field) {
        this.password = password;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.field = field;
    }

    // 연결 관계
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Project> ownedProjects;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectUser> projectUsers;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Shortcut> shortcuts;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts;
}