package com.blackjack.jraoms.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InterviewerNotificationDto {
    private int candidateId;
    private int vacancyId;
    private LocalDate interviewDate;
    private String action;
    private String interviewFormat;
}
