package com.hyupmin.domain.timepoll;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.hyupmin.domain.project.Project;
import com.hyupmin.domain.timeResponse.TimeResponse;
import com.hyupmin.domain.user.User;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "time_polls")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimePoll {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pollPk;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_pk", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_user_pk", nullable = false)
    private User creator;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private LocalTime startTimeOfDay;

    @Column(nullable = false)
    private LocalTime endTimeOfDay;

    @OneToMany(mappedBy = "poll", cascade = CascadeType.ALL)
    private List<TimeResponse> responses;
}