package com.blackjack.jraoms.dto;

import java.time.LocalDate;
import java.util.List;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VacancyDto {
	
	private int vacancyId;
	@NotEmpty
	private String positionName;
	@NotEmpty
	private String posts;
	@NotEmpty
	private List<String> workingDays;
	@NotEmpty
	private String workingHourFrom;
	@NotEmpty
	private String workingHourTo;
	@NotEmpty
	private String salary;
	@NotEmpty
	private String jobType;
	@NotEmpty
	private String requirements;
	@NotEmpty
	private String responsibilities;
	@NotEmpty
	private String description;
	@NotEmpty
	private String preference;
	@NotNull
	private Integer departmentId;
	
	private String companyName;
	
	private Integer numberOfApplyers;
	private LocalDate endDate;
	private LocalDate startDate;
	private Integer companyId;
	private String departmentName;
	
	
}
