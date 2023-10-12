package com.blackjack.jraoms.service;

import java.util.Optional;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blackjack.jraoms.entity.Candidate;
import com.blackjack.jraoms.entity.CandidateCV;
import com.blackjack.jraoms.repository.CandidateCVRepository;


@Service
@AllArgsConstructor
public class CandidateCVService {

	private final CandidateCVRepository candidateCVRepo;
	
	 public CandidateCV saveCandidateCV(CandidateCV candidateCV) {
			return candidateCVRepo.save(candidateCV);
		 }

	 public Optional<CandidateCV> findCVById(Integer id) {
			return candidateCVRepo.findById(id);
		 }
}
