package com.blackjack.jraoms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blackjack.jraoms.entity.InterviewHistory;
import com.blackjack.jraoms.repository.InterviewHistoryRepository;

@Service
public class InterviewHistoryService {

	@Autowired
	InterviewHistoryRepository interviewHistoryRepo;
	
	public InterviewHistory saveHistory(InterviewHistory history) {
		return interviewHistoryRepo.save(history);
	}
}
