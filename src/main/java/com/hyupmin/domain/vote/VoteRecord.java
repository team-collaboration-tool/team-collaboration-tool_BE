package com.hyupmin.domain.vote;

import jakarta.persistence.*;
import lombok.AllArgsConstructor; // 1. import
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.hyupmin.domain.user.User;

@Entity
@Table(name = "vote_response")
@Getter
@NoArgsConstructor
@AllArgsConstructor // 3. 추가
@Builder
public class VoteRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long responsePk;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vote_option_id", nullable = false)
    private VoteOption voteOption;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_pk", nullable = false)
    private User user;
}