package com.blackjack.jraoms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CandidateDto {
	private String name;
	private String dob;
	private String gender;
	private String phone;
	private String email;
	private String degree;
	private String technical;
	private String language;
	private String level;
	private String mainTechnical;
	private String experiences;
	private String salary;
	private String vacancyId;

}
