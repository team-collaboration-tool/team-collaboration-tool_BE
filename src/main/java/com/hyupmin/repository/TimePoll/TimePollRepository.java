package com.hyupmin.repository.TimePoll;

import com.hyupmin.domain.timepoll.TimePoll;
import com.hyupmin.domain.timeResponse.TimeResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TimePollRepository extends JpaRepository<TimePoll, Long> {
    //프로젝트별+마감기한 오늘(포함) 이후인 목록 리스트
    List<TimePoll> findByProject_ProjectPkAndEndDateGreaterThanEqual(
            Long projectPk,
            LocalDate today
    );
}

