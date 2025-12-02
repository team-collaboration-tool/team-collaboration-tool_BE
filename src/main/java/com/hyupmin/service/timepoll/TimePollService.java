package com.hyupmin.service.timepoll;

import lombok.RequiredArgsConstructor;
import com.hyupmin.domain.project.Project;
import com.hyupmin.repository.project.ProjectRepository; //수정
import com.hyupmin.domain.timepoll.TimePoll;
import com.hyupmin.domain.timeResponse.TimeResponse;
import com.hyupmin.domain.user.User;
import com.hyupmin.repository.user.UserRepository; //수정
import com.hyupmin.dto.timepoll.TimePollDto;
import com.hyupmin.repository.TimePoll.TimePollRepository;
import com.hyupmin.repository.TimePoll.TimeResponseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

    // 1. 투표 생성
    public void createTimePoll(TimePollDto.CreateRequest request) {
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));
        User creator = userRepository.findById(request.getCreatorId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // duration(일수)를 가지고 endDate 계산
        TimePoll poll = TimePoll.builder()
                .project(project)
                .creator(creator)
                .title(request.getTitle())
                .startDate(request.getStartDate())
                .endDate(request.getStartDate().plusDays(request.getDuration() - 1)) // 날짜 계산
                .startTimeOfDay(request.getStartTimeOfDay())
                .endTimeOfDay(request.getEndTimeOfDay())
                .build();

        timePollRepository.save(poll);
    }

    // 2. 투표 목록 조회 (Summary)
    @Transactional(readOnly = true)
    public List<TimePollDto.PollSummary> getPollList(Long projectId) {

        return timePollRepository.findAll().stream()
                .filter(p -> p.getProject().getProjectPk().equals(projectId))
                .map(p -> {

                    long days = ChronoUnit.DAYS.between(p.getStartDate(), p.getEndDate()) + 1;

                    return TimePollDto.PollSummary.builder()
                            .pollId(p.getPollPk())
                            .title(p.getTitle())
                            .startDate(p.getStartDate())             // 그대로 LocalDate 사용
                            .endDate(p.getEndDate())
                            .duration((int) days)                    // 계산된 며칠짜리
                            .startTime(p.getStartTimeOfDay())        // LocalTime 그대로
                            .endTime(p.getEndTimeOfDay())
                            .userCount(
                                    p.getResponses() == null ? 0 : p.getResponses().size()
                            )
                            .build();
                })
                .collect(Collectors.toList());
    }

    // 3. 응답 제출
    public void submitResponse(TimePollDto.SubmitRequest request) {
        // (기존 로직과 동일: 삭제 후 재생성)
        timeResponseRepository.deleteByPoll_PollPkAndUser_UserPk(request.getPollId(), request.getUserId());

        TimePoll poll = timePollRepository.findById(request.getPollId()).orElseThrow();
        User user = userRepository.findById(request.getUserId()).orElseThrow();

        List<TimeResponse> responses = request.getAvailableTimes().stream()
                .map(t -> TimeResponse.builder()
                        .poll(poll)
                        .user(user)
                        .startTimeUtc(LocalDateTime.parse(t.getStart())) // String -> LocalDateTime
                        .endTimeUtc(LocalDateTime.parse(t.getEnd()))
                        .build())
                .collect(Collectors.toList());

        timeResponseRepository.saveAll(responses);
    }

    // ★ 4. 상세 조회 (2차원 배열 히트맵 변환 로직) ★
    @Transactional(readOnly = true)
    public TimePollDto.DetailResponse getPollDetailGrid(Long pollId) {
        TimePoll poll = timePollRepository.findById(pollId).orElseThrow();
        List<TimeResponse> allResponses = timeResponseRepository.findByPoll_PollPk(pollId);

        // A. 시간 슬롯(행/열) 계산
        long totalDays = Duration.between(poll.getStartDate().atStartOfDay(), poll.getEndDate().atStartOfDay()).toDays() + 1;
        long minutesPerDay = Duration.between(poll.getStartTimeOfDay(), poll.getEndTimeOfDay()).toMinutes();
        int slotsPerDay = (int) (minutesPerDay / 30); // 30분 단위

        // B. 2차원 배열 초기화 [날짜수][시간슬롯수] (예: 7일 x 18타임)
        int[][] grid = new int[(int) totalDays][slotsPerDay];

        // C. 모든 응답을 순회하며 Grid 채우기
        for (TimeResponse r : allResponses) {
            fillGrid(grid, r, poll.getStartDate().atStartOfDay(), poll.getStartTimeOfDay(), slotsPerDay);
        }

        // D. 라벨 생성 (프론트 편의용)
        List<String> dateLabels = new ArrayList<>();
        poll.getStartDate().datesUntil(poll.getEndDate().plusDays(1))
                .forEach(d -> dateLabels.add(d.format(DateTimeFormatter.ofPattern("MM-dd"))));

        return TimePollDto.DetailResponse.builder()
                .pollId(poll.getPollPk())
                .title(poll.getTitle())
                .gridData(grid) // 2차원 배열 리턴
                .dateLabels(dateLabels)
                .build();
    }

    // Helper: 특정 응답 시간대를 Grid 인덱스로 변환하여 카운트 증가
    private void fillGrid(int[][] grid, TimeResponse r, LocalDateTime pollStartDateTime, LocalTime dayStartTime, int slotsPerDay) {
        // 이 로직은 꽤 정교해야 합니다.
        // 간단히 설명하면: Response의 시작/종료 시간을 "몇 번째 날", "몇 번째 슬롯"인지 계산해서 grid[day][slot]++ 해줍니다.

        LocalDateTime current = r.getStartTimeUtc();
        while (current.isBefore(r.getEndTimeUtc())) {
            // 1. 날짜 인덱스 계산
            long dayIdx = Duration.between(pollStartDateTime.toLocalDate().atStartOfDay(), current.toLocalDate().atStartOfDay()).toDays();

            // 2. 시간 슬롯 인덱스 계산
            long minuteOffset = Duration.between(dayStartTime, current.toLocalTime()).toMinutes();
            int slotIdx = (int) (minuteOffset / 30);

            // 3. 범위 체크 후 카운트 증가
            if (dayIdx >= 0 && dayIdx < grid.length && slotIdx >= 0 && slotIdx < slotsPerDay) {
                grid[(int) dayIdx][slotIdx]++;
            }

            current = current.plusMinutes(30); // 30분씩 전진
        }
    }
}