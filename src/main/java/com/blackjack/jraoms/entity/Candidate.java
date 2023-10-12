package com.blackjack.jraoms.entity;

import java.time.LocalDate;
import java.util.List;

import com.blackjack.jraoms.dto.CandidateReportDto;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedStoredProcedureQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.SqlResultSetMapping;
import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
@Entity
@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@NamedStoredProcedureQuery(
	    name = "Candidate.getAllCandidatesAndVacancies", 
	    procedureName = "get_all_candidates_and_vacancies", 
	    resultSetMappings = "CandidateReportDto"
	)
@SqlResultSetMapping(
	    name = "CandidateReportDto",
	    classes = @ConstructorResult(
	        targetClass = CandidateReportDto.class,
	        columns = {
	            @ColumnResult(name = "id", type = Integer.class),
	            @ColumnResult(name = "name", type = String.class),
	            @ColumnResult(name = "date", type = LocalDate.class),
	            @ColumnResult(name = "cv_status", type = String.class),
	            @ColumnResult(name = "dob", type = String.class),
	            @ColumnResult(name = "gender", type = String.class),
	            @ColumnResult(name = "phone", type = String.class),
	            @ColumnResult(name = "email", type = String.class),
	            @ColumnResult(name = "degree", type = String.class),
	            @ColumnResult(name = "technical", type = String.class),
	            @ColumnResult(name = "language", type = String.class),
	            @ColumnResult(name = "position", type = String.class),
	            @ColumnResult(name = "level", type = String.class),
	            @ColumnResult(name = "main_technical", type = String.class),
	            @ColumnResult(name = "experiences", type = String.class),
	            @ColumnResult(name = "salary", type = String.class),
	            @ColumnResult(name = "vacancy_id", type = Integer.class),
	            @ColumnResult(name = "candidate_cv_id", type = Integer.class)
	        }
	    )
	)
public class Candidate {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(length = 45, nullable = false)
	private String name;

	@Column(length = 45, nullable = false)
	private String dob;
	
	@Column(length = 45, nullable = false)
	private String gender;

	@Column(length = 45, nullable = false)
	private String phone;

	@Column(length = 45, nullable = false)
	private String email;
	

	@Column(length = 200, nullable = false)
	private String degree;
	
	
	@Column(length = 200, nullable = false)
	private String technical;
	

	@Column(length = 200, nullable = false)
	private String language;

	@Column(length = 200, nullable = false)
	private String level;

	@Column(length = 200, nullable = false)
	private String mainTechnical;
	
	@Column(length = 45, nullable = false)
	private String experiences;
	
	@Column(length = 45, nullable = false)
	private String salary;
	
	@Column(nullable = false)
	private LocalDate date;
	
	@Column(length = 45, nullable = false)
	@Enumerated(EnumType.STRING)
	private CvStatus cvStatus;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "candidate_cv_id",nullable = false)
	private CandidateCV candidateCV;
	
	@ManyToOne( cascade = CascadeType.ALL)
	@JoinColumn(name = "vacancy_id",nullable = false)
	@JsonBackReference
	private Vacancy vacancy;
	
	@OneToMany(mappedBy = "candidate",cascade = CascadeType.ALL)
	@JsonManagedReference
	private List<StatusHistory> statusHistories;
	
	@OneToMany(mappedBy = "candidate",cascade = CascadeType.ALL)
	@JsonManagedReference
	private List<InterviewHistory> interviewHistories;
	
	@Column(length = 45, nullable = true)
	private LocalDate joinDate;
	
	@Column(length = 45, nullable = true)
	private String wages;
}
