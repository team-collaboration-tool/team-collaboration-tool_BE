package com.hyupmin.repository.TimePoll;

import com.hyupmin.domain.timepoll.TimePoll;
import com.hyupmin.domain.timeResponse.TimeResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TimePollRepository extends JpaRepository<TimePoll, Long> {

}

