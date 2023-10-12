package com.blackjack.jraoms.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentRequestDto {
	private String interviewStage;
	private LocalDate interviewDate;
	private String interviewFormat;
	
}
