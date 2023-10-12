package com.blackjack.jraoms.dto;

import java.time.LocalDate;




import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CandidateReportDto {
	private int id;
	private String name;
	private LocalDate date;
	private String cv_status;
	private String dob;
	private String gender;
	private String phone;
	private String email;
	private String degree;
	private String technical;
	private String language;
	private String position;
	private String level;
	private String main_technical;
	private String experiences;
	private String salary;
	private int candidate_cv_id;
	private int vacancy_id;
	private String interview_date;
	private String wages;
	private String join_date;
	public CandidateReportDto(int id, String name, LocalDate date, String cv_status, String dob, String gender,
			String phone, String email, String degree, String technical, String language, String position, String level,
			String main_technical, String experiences, String salary) {
		super();
		this.id = id;
		this.name = name;
		this.date = date;
		this.cv_status = cv_status;
		this.dob = dob;
		this.gender = gender;
		this.phone = phone;
		this.email = email;
		this.degree = degree;
		this.technical = technical;
		this.language = language;
		this.position = position;
		this.level = level;
		this.main_technical = main_technical;
		this.experiences = experiences;
		this.salary = salary;
	}
	public CandidateReportDto(int id, String name, LocalDate date, String cv_status, String dob, String gender,
			String phone, String email, String degree, String technical, String language, String position, String level,
			String main_technical, String experiences, String salary, String interview_date) {
		super();
		this.id = id;
		this.name = name;
		this.date = date;
		this.cv_status = cv_status;
		this.dob = dob;
		this.gender = gender;
		this.phone = phone;
		this.email = email;
		this.degree = degree;
		this.technical = technical;
		this.language = language;
		this.position = position;
		this.level = level;
		this.main_technical = main_technical;
		this.experiences = experiences;
		this.salary = salary;
		this.interview_date = interview_date;
	}
	public CandidateReportDto(int id, String name, LocalDate date, String cv_status, String dob, String gender,
			String phone, String email, String degree, String technical, String language, String position, String level,
			String main_technical, String experiences, String salary, String interview_date, String wages,
			String join_date) {
		super();
		this.id = id;
		this.name = name;
		this.date = date;
		this.cv_status = cv_status;
		this.dob = dob;
		this.gender = gender;
		this.phone = phone;
		this.email = email;
		this.degree = degree;
		this.technical = technical;
		this.language = language;
		this.position = position;
		this.level = level;
		this.main_technical = main_technical;
		this.experiences = experiences;
		this.salary = salary;
		this.interview_date = interview_date;
		this.wages = wages;
		this.join_date = join_date;
	}
	
}


