package com.blackjack.jraoms.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WelcomeMailDto {
    private String candidateEmail;
    private String candidateName;
    private String vacancyName;
    private String companyName;
}
