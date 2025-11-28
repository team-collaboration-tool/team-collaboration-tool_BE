package com.hyupmin.repository.vote;

import com.hyupmin.domain.user.User;
import com.hyupmin.domain.vote.Vote;
import com.hyupmin.domain.vote.VoteOption;
import com.hyupmin.domain.vote.VoteRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VoteRecordRepository extends JpaRepository<VoteRecord, Long> {

    boolean existsByUserAndVoteOption_Vote(User user, Vote vote);

    List<VoteRecord> findByUserAndVoteOption_Vote(User user, Vote vote);

    // 같은 옵션을 두 번 찍는 것 방지용
    boolean existsByUserAndVoteOption(User user, VoteOption voteOption);
}