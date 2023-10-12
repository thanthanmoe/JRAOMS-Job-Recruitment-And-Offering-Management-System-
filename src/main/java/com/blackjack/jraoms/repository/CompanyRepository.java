package com.blackjack.jraoms.repository;

import com.blackjack.jraoms.entity.Company;

import java.util.Optional;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Integer>,DataTablesRepository<Company, Integer> {
	
	Optional<Company> findByName(String name);
}
