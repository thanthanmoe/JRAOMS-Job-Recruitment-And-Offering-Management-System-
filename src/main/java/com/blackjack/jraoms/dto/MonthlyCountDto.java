package com.blackjack.jraoms.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MonthlyCountDto {
    private int year;
    private int month;
    private long count;


}
