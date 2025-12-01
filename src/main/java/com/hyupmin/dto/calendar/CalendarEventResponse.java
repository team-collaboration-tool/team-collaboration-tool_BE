package com.hyupmin.dto.calendar;

import lombok.Builder;
import lombok.Getter;
import com.hyupmin.domain.calendar.CalendarEvent;
import com.hyupmin.domain.user.User; // User import

import java.time.LocalDateTime;
import java.util.List; // List import
import java.util.stream.Collectors; // Collectors import

@Getter
@Builder
public class CalendarEventResponse {

    @Getter
    @Builder
    public static class ParticipantDTO {
        private Long userPk;
        private String name;
        private String email;

        public static ParticipantDTO from(User user) {
            return ParticipantDTO.builder()
                    .userPk(user.getUserPk())
                    .name(user.getName())
                    .email(user.getEmail())
                    .build();
        }
    }

    private Long eventPk;
    private Long projectPk;
    private Long createUserId;
    private String createUserName;
    private String title;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String description;
    private List<ParticipantDTO> participants;

    public static CalendarEventResponse from(CalendarEvent event) {
        List<ParticipantDTO> participantDTOs = event.getParticipants().stream()
                .map(ParticipantDTO::from)
                .collect(Collectors.toList());

        return CalendarEventResponse.builder()
                .eventPk(event.getEventPk())
                .projectPk(event.getProject().getProjectPk())
                .createUserId(event.getCreateUser().getUserPk())
                .createUserName(event.getCreateUser().getName())
                .title(event.getTitle())
                .startTime(event.getStartTime())
                .endTime(event.getEndTime())
                .description(event.getDescription())
                .participants(participantDTOs)
                .build();
    }
}