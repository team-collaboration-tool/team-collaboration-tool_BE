package com.hyupmin.repository.TimePoll;

import com.hyupmin.domain.timeResponse.TimeResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TimeResponseRepository extends JpaRepository<TimeResponse, Long> {
    // 특정 투표에 대한 모든 응답 조회
    List<TimeResponse> findByPoll_PollPk(Long pollPk);

    // 특정 투표에 대한 특정 유저의 기존 응답 삭제 (수정 시 사용)
    void deleteByPoll_PollPkAndUser_UserPk(Long pollPk, Long userPk);

    // 특정 투표, 특정 유저 응답 조회
    List<TimeResponse> findByPoll_PollPkAndUser_UserPk(Long pollPk, Long userPk);
}
