package com.hyupmin.domain.calendar;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.hyupmin.domain.project.Project;
import com.hyupmin.domain.user.User;

import java.time.LocalDateTime;
import java.util.HashSet; // Set import
import java.util.Set; // Set import

@Entity
@Getter
@Setter
@Table(name = "calendar_events")
@NoArgsConstructor
public class CalendarEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventPk; // DBML: event_pk

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_pk", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "create_user_pk", nullable = false)
    private User createUser;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "calendar_event_participants", // 중간 테이블
            joinColumns = @JoinColumn(name = "event_pk"),
            inverseJoinColumns = @JoinColumn(name = "user_pk")
    )
    private Set<User> participants = new HashSet<>();

    @Column(nullable = false)
    private String title;

    @Column(length = 20)
    private String color;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime; // DBML: start_time (timestamp)

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime; // DBML: end_time (timestamp)

    @Column(columnDefinition = "TEXT")
    private String description; // DBML: description

    public CalendarEvent(Project project, User createUser, String title, String color, LocalDateTime startTime, LocalDateTime endTime, String description) {
        this.project = project;
        this.createUser = createUser;
        this.title = title;
        this.color = color;
        this.startTime = startTime;
        this.endTime = endTime;
        this.description = description;
    }

    public void update(String title, String color, LocalDateTime startTime, LocalDateTime endTime, String description, Set<User> participants) {
        if (title != null) this.title = title;
        if (color != null) this.color = color;
        if (startTime != null) this.startTime = startTime;
        if (endTime != null) this.endTime = endTime;
        if (description != null) this.description = description;
        if (participants != null) this.participants = participants;
    }

    public boolean isParticipant(String userEmail) {
        return participants.stream()
                .anyMatch(user -> user.getEmail().equals(userEmail));
    }
}