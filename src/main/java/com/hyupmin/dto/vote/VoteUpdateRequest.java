package com.hyupmin.dto.vote;

import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class VoteUpdateRequest {
    private String title;
    private LocalDateTime endTime;
    private Boolean allowMultipleChoices;
    private Boolean isAnonymous;

}
