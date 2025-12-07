package com.hyupmin.service.timepoll;

import lombok.RequiredArgsConstructor;
import com.hyupmin.domain.project.Project;
import com.hyupmin.repository.project.ProjectRepository;
import com.hyupmin.domain.timepoll.TimePoll;
import com.hyupmin.domain.timeResponse.TimeResponse;
import com.hyupmin.domain.user.User;
import com.hyupmin.repository.user.UserRepository;
import com.hyupmin.dto.timepoll.TimePollDto;
import com.hyupmin.repository.TimePoll.TimePollRepository;
import com.hyupmin.repository.TimePoll.TimeResponseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Transactional
public class TimePollService {

    private final TimePollRepository timePollRepository;
    private final TimeResponseRepository timeResponseRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public void createTimePoll(TimePollDto.CreateRequest request) {
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));
        User creator = userRepository.findById(request.getCreatorId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        TimePoll poll = TimePoll.builder()
                .project(project)
                .creator(creator)
                .title(request.getTitle())
                .startDate(request.getStartDate())
                .endDate(request.getStartDate().plusDays(request.getDuration() - 1))
                .startTimeOfDay(request.getStartTimeOfDay())
                .endTimeOfDay(request.getEndTimeOfDay())
                .build();

        timePollRepository.save(poll);
    }

    @Transactional(readOnly = true)
    public List<TimePollDto.PollSummary> getPollList(Long projectId) {

        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));

        List<TimePoll> polls =
                timePollRepository.findByProject_ProjectPkAndEndDateGreaterThanEqual(projectId, today);

        return polls.stream()
                .map(p -> {
                    long days = ChronoUnit.DAYS.between(p.getStartDate(), p.getEndDate()) + 1;

                    return TimePollDto.PollSummary.builder()
                            .pollId(p.getPollPk())
                            .title(p.getTitle())
                            .startDate(p.getStartDate())
                            .endDate(p.getEndDate())
                            .duration((int) days)
                            .startTime(p.getStartTimeOfDay())
                            .endTime(p.getEndTimeOfDay())
                            .userCount(
                                    p.getResponses() == null ? 0 : p.getResponses().size()
                            )
                            .build();
                })
                .collect(Collectors.toList());
    }

    public void submitResponse(TimePollDto.SubmitRequest request) {
        timeResponseRepository.deleteByPoll_PollPkAndUser_UserPk(request.getPollId(), request.getUserId());

        TimePoll poll = timePollRepository.findById(request.getPollId()).orElseThrow();
        User user = userRepository.findById(request.getUserId()).orElseThrow();

        List<TimeResponse> responses = request.getAvailableTimes().stream()
                .map(t -> TimeResponse.builder()
                        .poll(poll)
                        .user(user)
                        .startTimeUtc(LocalDateTime.parse(t.getStart()))
                        .endTimeUtc(LocalDateTime.parse(t.getEnd()))
                        .build())
                .collect(Collectors.toList());

        timeResponseRepository.saveAll(responses);
    }

    @Transactional(readOnly = true)
    public TimePollDto.DetailResponse getPollDetailGrid(Long pollId, Long userId) {
        TimePoll poll = timePollRepository.findById(pollId).orElseThrow();

        List<TimeResponse> allResponses = timeResponseRepository.findByPoll_PollPk(pollId);
        List<TimeResponse> myResponses =
                timeResponseRepository.findByPoll_PollPkAndUser_UserPk(pollId, userId);

        long totalDays = Duration.between(
                poll.getStartDate().atStartOfDay(),
                poll.getEndDate().atStartOfDay()
        ).toDays() + 1;

        long minutesPerDay = Duration.between(
                poll.getStartTimeOfDay(),
                poll.getEndTimeOfDay()
        ).toMinutes();

        int slotsPerDay = (int) (minutesPerDay / 30); // 30분 단위

        int[][] teamGrid = new int[(int) totalDays][slotsPerDay];
        int[][] myGrid = new int[(int) totalDays][slotsPerDay];

        LocalDateTime pollStartDateTime = poll.getStartDate().atStartOfDay();
        for (TimeResponse r : allResponses) {
            fillGrid(teamGrid, r, pollStartDateTime, poll.getStartTimeOfDay(), slotsPerDay);
        }

        for (TimeResponse r : myResponses) {
            fillGrid(myGrid, r, pollStartDateTime, poll.getStartTimeOfDay(), slotsPerDay);
        }

        List<String> dateLabels = new ArrayList<>();
        poll.getStartDate().datesUntil(poll.getEndDate().plusDays(1))
                .forEach(d -> dateLabels.add(d.format(DateTimeFormatter.ofPattern("MM-dd"))));

        List<String> timeLabels = new ArrayList<>();
        LocalTime t = poll.getStartTimeOfDay();
        while (t.isBefore(poll.getEndTimeOfDay())) {
            timeLabels.add(t.format(DateTimeFormatter.ofPattern("HH:mm")));
            t = t.plusMinutes(30);
        }

        return TimePollDto.DetailResponse.builder()
                .pollId(poll.getPollPk())
                .title(poll.getTitle())
                .teamGrid(teamGrid)
                .myGrid(myGrid)
                .dateLabels(dateLabels)
                .timeLabels(timeLabels)
                .build();
    }

    private void fillGrid(int[][] grid, TimeResponse r, LocalDateTime pollStartDateTime, LocalTime dayStartTime, int slotsPerDay) {

        LocalDateTime current = r.getStartTimeUtc();
        while (current.isBefore(r.getEndTimeUtc())) {

            long dayIdx = Duration.between(pollStartDateTime.toLocalDate().atStartOfDay(), current.toLocalDate().atStartOfDay()).toDays();

            long minuteOffset = Duration.between(dayStartTime, current.toLocalTime()).toMinutes();
            int slotIdx = (int) (minuteOffset / 30);

            if (dayIdx >= 0 && dayIdx < grid.length && slotIdx >= 0 && slotIdx < slotsPerDay) {
                grid[(int) dayIdx][slotIdx]++;
            }

            current = current.plusMinutes(30);
        }
    }
}