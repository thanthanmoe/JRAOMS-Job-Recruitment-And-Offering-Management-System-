package com.blackjack.jraoms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.blackjack.jraoms.entity.InterviewProcess;

@Repository
public interface InterviewProcessRepository extends JpaRepository<InterviewProcess, Integer> {
    // Define other custom methods if necessary
}
