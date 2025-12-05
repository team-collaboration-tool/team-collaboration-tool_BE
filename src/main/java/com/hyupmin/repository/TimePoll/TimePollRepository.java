package com.hyupmin.repository.TimePoll;

import com.hyupmin.domain.timepoll.TimePoll;
import com.hyupmin.domain.timeResponse.TimeResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TimePollRepository extends JpaRepository<TimePoll, Long> {
    List<TimePoll> findByProject_ProjectPkAndEndDateGreaterThanEqual(
            Long projectPk,
            LocalDate today
    );
}

