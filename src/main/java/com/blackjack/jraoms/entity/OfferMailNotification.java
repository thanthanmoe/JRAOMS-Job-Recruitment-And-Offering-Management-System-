package com.blackjack.jraoms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OfferMailNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private LocalDateTime receive;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "vacancy_id")
    private Vacancy vacancy;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "candidate_id")
    private Candidate candidate;

}
