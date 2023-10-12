package com.blackjack.jraoms.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OfferMailNotificationDto {
    private int vacancyId;
    private int candidateId;
}
