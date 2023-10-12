package com.blackjack.jraoms.entity;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonBackReference;

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
public class StatusHistory {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column(nullable = false)
	private LocalDate date;
	
	@Column(length = 45, nullable = false)
	@Enumerated(EnumType.STRING)
	private CvStatus action;
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "candidate_id",nullable = false)
	@JsonBackReference
	private Candidate candidate;
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	
}
