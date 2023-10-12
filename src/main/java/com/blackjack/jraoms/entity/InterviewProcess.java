package com.blackjack.jraoms.entity;

import java.time.LocalDate;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "interview_process")
public class InterviewProcess {
	
		@Id
	    private int id;
	    private String position;
	    private LocalDate start_date;
	    private LocalDate end_date;
	    private int total_candidates;
	    private int not_interview;
	    private int interview_candidate;
	    private int receive;
	    private int view;
	    private int considering;
	    private int passed;
	    private int pending;
	    private int cancel;

}
