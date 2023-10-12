package com.blackjack.jraoms.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.blackjack.jraoms.entity.Vacancy;
import org.springframework.data.jpa.repository.Query;

public interface VacancyRepository extends JpaRepository<Vacancy,Integer>{

	List<Vacancy> findBystatus(String status);
	List<Vacancy> findByStatusIn(List<String> statusList);
	@Query(value = "WITH Months AS (\n" +
			"    SELECT 1 AS month_number UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL \n" +
			"    SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL \n" +
			"    SELECT 8 UNION ALL SELECT 9 UNION ALL SELECT 10 UNION ALL SELECT 11 UNION ALL \n" +
			"    SELECT 12\n" +
			"),\n" +
			"YearData AS (\n" +
			"    SELECT DISTINCT YEAR(v.start_date) as year FROM vacancy v\n" +
			")\n" +
			"\n" +
			"SELECT \n" +
			"    y.year,\n" +
			"    m.month_number AS month,\n" +
			"    COALESCE(c.count, 0) AS count\n" +
			"FROM \n" +
			"    Months m\n" +
			"CROSS JOIN YearData y\n" +
			"LEFT JOIN (\n" +
			"    SELECT \n" +
			"        YEAR(v.start_date) as year,\n" +
			"        MONTH(v.start_date) as month,\n" +
			"        COUNT(v.start_date) as count\n" +
			"    FROM vacancy v \n" +
			"    GROUP BY \n" +
			"        YEAR(v.start_date), \n" +
			"        MONTH(v.start_date)\n" +
			") c ON c.year = y.year AND c.month = m.month_number\n" +
			"ORDER BY \n" +
			"    y.year, \n" +
			"    m.month_number",nativeQuery = true)
	List<Object[]> findMonthlyCreateActions();

}
