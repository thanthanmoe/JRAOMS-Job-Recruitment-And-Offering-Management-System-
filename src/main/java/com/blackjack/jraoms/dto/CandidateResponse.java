package com.blackjack.jraoms.dto;

import com.blackjack.jraoms.entity.Candidate;
import com.blackjack.jraoms.entity.Vacancy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CandidateResponse {

	    private Candidate candidate;
	    private Vacancy vacancy;
	    private String cvData;
	    private String cvType;
	    // getter and setter methods...
	
}
