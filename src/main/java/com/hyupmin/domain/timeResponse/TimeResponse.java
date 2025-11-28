package com.hyupmin.domain.timeResponse;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.hyupmin.domain.timepoll.TimePoll;
import com.hyupmin.domain.user.User;
import java.time.LocalDateTime;

@Entity
@Table(name = "time_response")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long responsePk;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poll_pk", nullable = false)
    private TimePoll poll;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_pk", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime startTimeUtc;

    @Column(nullable = false)
    private LocalDateTime endTimeUtc;
}