package com.blackjack.jraoms.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.blackjack.jraoms.entity.CvStatus;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;
import com.blackjack.jraoms.dto.CandidateReportDto;
import com.blackjack.jraoms.entity.Candidate;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Integer>, DataTablesRepository<Candidate,Integer>{
    List<Candidate> findByVacancyId(int id);
    List<Candidate> findByCvStatus(String cvStatus);
	 
    @Procedure(name = "Candidate.getAllCandidatesAndVacancies")
    List<CandidateReportDto> getAllCandidatesAndVacancies();
    
    @Procedure("sp_getCandidatesAndVacancies")
    void getCandidatesAndVacancies(Date startDate,Date endDate);
    
    @Procedure("sp_getCandidatesByStatus")
    void getCandidatesByStatus(String status);
    
    @Procedure("get_countByVacancy_id")
    void getCountByVacancy_id();
   
    @Procedure("sp_getCandidatesByVacancyId")
    void getCandidatesByVacancyId(Integer vacancyId);

    @Query(value = "WITH Months AS (\n" +
            "    SELECT 1 AS month_number UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL \n" +
            "    SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL \n" +
            "    SELECT 8 UNION ALL SELECT 9 UNION ALL SELECT 10 UNION ALL SELECT 11 UNION ALL \n" +
            "    SELECT 12\n" +
            "),\n" +
            "YearData AS (\n" +
            "    SELECT DISTINCT YEAR(v.date) as year FROM candidate v\n" +
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
            "        YEAR(v.date) as year,\n" +
            "        MONTH(v.date) as month,\n" +
            "        COUNT(v.date) as count\n" +
            "    FROM candidate v \n" +
            "    GROUP BY \n" +
            "        YEAR(v.date), \n" +
            "        MONTH(v.date)\n" +
            ") c ON c.year = y.year AND c.month = m.month_number\n" +
            "ORDER BY \n" +
            "    y.year, \n" +
            "    m.month_number\n",nativeQuery = true)
    List<Object[]> findMonthlyCounts();
    
    public Optional<Candidate> findByEmailAndVacancyId(String email,int id);
    List<Candidate> findByVacancyIdAndCvStatus(int id, CvStatus status);
}
