package com.hyupmin.repository.vote;

import com.hyupmin.domain.vote.VoteOption;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteOptionRepository extends JpaRepository<VoteOption, Long> {
}
