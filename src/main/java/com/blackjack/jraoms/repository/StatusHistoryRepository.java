package com.blackjack.jraoms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.blackjack.jraoms.entity.StatusHistory;

@Repository
public interface StatusHistoryRepository extends JpaRepository<StatusHistory, Integer>{

}
