package com.blackjack.jraoms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterviewerNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private LocalDateTime receive;
    private LocalDate interviewDate;
    private String action;
    private String interviewFormat;
    private int vacancyId;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "candidate_id")
    public Candidate candidate;

}
