package com.blackjack.jraoms.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.blackjack.jraoms.entity.CandidateCV;

@Repository
public interface CandidateCVRepository extends JpaRepository<CandidateCV, Integer>{

}
