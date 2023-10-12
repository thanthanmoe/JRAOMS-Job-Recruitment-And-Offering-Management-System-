package com.blackjack.jraoms.entity;
import java.time.LocalDate;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vacancy {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(length = 100, nullable = false)
	private String position;

	@Column(nullable = false)
	private int requirePosition;

	@Column(length = 45, nullable = false)
	private String workingDay;

	@Column(length = 45, nullable = false)
	private String workingHours;

	@Column(length = 45, nullable = false)
	private String salary;

	@Column(length = 45, nullable = false)
	private String jobType;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String description;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String responsibilities;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String requirements;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String preferences;

	@Column(nullable = false)
	private LocalDate startDate;

	@Column(nullable = false)
	private LocalDate endDate;

	@Column(length = 45, nullable = false)
	private String status;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "department_id",nullable = false)
	private Department department;

	@Transient // This field is not persisted in the database
	private int daysLeft;
	
	@Transient
	private int applyerAmount;
}