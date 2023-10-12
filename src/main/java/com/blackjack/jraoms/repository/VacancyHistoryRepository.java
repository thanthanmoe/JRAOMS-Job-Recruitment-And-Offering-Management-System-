package com.blackjack.jraoms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.blackjack.jraoms.entity.VacancyHistory;

public interface VacancyHistoryRepository extends JpaRepository<VacancyHistory,Integer>{

	List<VacancyHistory> findByVacancy_id(Integer id);

	@Query("SELECT CONCAT(YEAR(v.date), '-', FUNCTION('LPAD', CAST(MONTH(v.date) AS string), 2, '0')) as yearMonth, COUNT(v) as totalVacancy " +
			"FROM VacancyHistory v " +
			"WHERE v.action='create' " +
			"GROUP BY YEAR(v.date), MONTH(v.date), CONCAT(YEAR(v.date), '-', FUNCTION('LPAD', CAST(MONTH(v.date) AS string), 2, '0'))")
	List<Object[]> findMonthlyCreateActions();

}