package com.blackjack.jraoms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.blackjack.jraoms.entity.InterviewHistory;

@Repository
public interface InterviewHistoryRepository extends JpaRepository<InterviewHistory, Integer>{

}
