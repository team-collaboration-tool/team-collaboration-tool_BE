package com.hyupmin.domain.vote;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.ArrayList;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteOption {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    @Builder.Default
    private Integer count = 0; // 득표수

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vote_id")
    private Vote vote;

    @OneToMany(mappedBy = "voteOption",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @Builder.Default
    private List<VoteRecord> voteRecords = new ArrayList<>();

    public void setVote(Vote vote) {
        this.vote = vote;
    }


    public void addRecord(VoteRecord record) {
        this.voteRecords.add(record);
        record.setVoteOption(this);
    }

    public void increaseCount() {
        this.count++;
    }

    public void decreaseCount() {
        if (this.count > 0) {
            this.count--;
        }
    }
}