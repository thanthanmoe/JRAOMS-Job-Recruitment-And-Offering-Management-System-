package com.blackjack.jraoms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blackjack.jraoms.entity.StatusHistory;
import com.blackjack.jraoms.repository.StatusHistoryRepository;

@Service
public class StatusHistoryService {
	@Autowired
	StatusHistoryRepository statusHistoryRepo;
	
	public StatusHistory saveHistory(StatusHistory history) {
		return statusHistoryRepo.save(history);
	}
}