package com.blackjack.jraoms.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.blackjack.jraoms.dto.MonthlyCountDto;
import com.blackjack.jraoms.entity.CvStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.stereotype.Service;

import com.blackjack.jraoms.dto.CandidateReportDto;
import com.blackjack.jraoms.dto.ChartsDto;
import com.blackjack.jraoms.entity.Candidate;
import com.blackjack.jraoms.repository.CandidateRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import jakarta.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CandidateService {
	 @Autowired
	 CandidateRepository candidateRepo;
	  @PersistenceContext
	    private EntityManager em;
	    
	   
	 public Candidate saveCandidate(Candidate candidate) {
		 
		return candidateRepo.save(candidate);
	 }
	 
	 public Candidate candidate(Candidate candidate) {
		return candidateRepo.save(candidate);
	 }
	 
	 public List<Candidate> findAllCandidate() {
			return candidateRepo.findAll();
		 }

	 public Optional<Candidate> findCandidateById(Integer id) {
			return candidateRepo.findById(id);
	 }
	 
	 @Transactional
	    public DataTablesOutput<Candidate> getCandidate(DataTablesInput input) {
	        return candidateRepo.findAll(input);
	    }
	 public List<Candidate> findByCVStatus(String cvStatus) {
			return candidateRepo.findByCvStatus(cvStatus);
		 }
	 @Transactional()
	 public List<CandidateReportDto> getAllCandidatesAndVacancies() {
	        return candidateRepo.getAllCandidatesAndVacancies();
	    }

	 @Transactional()
	 public List<CandidateReportDto> fetchCandidatesAndVacancies(Date startDate,Date endDate) {
	        StoredProcedureQuery query = em.createStoredProcedureQuery("sp_getCandidatesAndVacancies");
	        query.registerStoredProcedureParameter(1, java.sql.Date.class, ParameterMode.IN);
	        query.registerStoredProcedureParameter(2, java.sql.Date.class, ParameterMode.IN);
	        query.setParameter(1, new java.sql.Date(startDate.getTime()));
	        query.setParameter(2, new java.sql.Date(endDate.getTime()));
	        query.execute();
	        List<Object[]> result = query.getResultList();
	        List<CandidateReportDto> candidates = new ArrayList<>();
	        for (Object[] row : result) {
	        	CandidateReportDto candidate = new CandidateReportDto(
	        			 (Integer) row[0], // id
	        		        (String) row[1],  // name
	        		        ((java.sql.Date) row[2]).toLocalDate(), // date
	        		        (String) row[3],  // cv_status
	        		        (String) row[4],  // dob
	        		        (String) row[5],  // gender
	        		        (String) row[6],  // phone
	        		        (String) row[7],  // email
	        		        (String) row[8],  // degree
	        		        (String) row[9],  // technical
	        		        (String) row[10], // language
	        		        (String) row[11], // position
	        		        (String) row[12], // level
	        		        (String) row[13], // main_technical
	        		        (String) row[14], // experiences
	        		        (String) row[15] // salary
	        		  
	        		    );
	            candidates.add(candidate);
	        }
	    
	        return candidates;
	    }

	 @Transactional()
	 public List<CandidateReportDto> fetchCandidatesByStatus(String status) {
	        StoredProcedureQuery query = em.createStoredProcedureQuery("sp_getCandidatesByStatus");
	        query.registerStoredProcedureParameter(1, String.class, ParameterMode.IN);
	        query.setParameter(1, status);

	        query.execute();

	        List<Object[]> result = query.getResultList();
	        List<CandidateReportDto> candidates = new ArrayList<>();
	        for (Object[] row : result) {
	        	CandidateReportDto candidate = new CandidateReportDto(
	        			 (Integer) row[0], // id
	        		        (String) row[1],  // name
	        		        ((java.sql.Date) row[2]).toLocalDate(), // date
	        		        (String) row[3],  // cv_status
	        		        (String) row[4],  // dob
	        		        (String) row[5],  // gender
	        		        (String) row[6],  // phone
	        		        (String) row[7],  // email
	        		        (String) row[8],  // degree
	        		        (String) row[9],  // technical
	        		        (String) row[10], // language
	        		        (String) row[11], // position
	        		        (String) row[12], // level
	        		        (String) row[13], // main_technical
	        		        (String) row[14], // experiences
	        		        (String) row[15],//salary
	        		        (String) row[16]//interview_date
	        		    );
	            candidates.add(candidate);
	        }
	    
	        return candidates;
	    }
	 public List<Candidate> findAllCandidateByVacancyId(int id){
		 return candidateRepo.findByVacancyId(id);
	 }
	
	 @Transactional()
	 public List<ChartsDto> fetchCountByVacancy_id(){
	     StoredProcedureQuery query = em.createStoredProcedureQuery("get_countByVacancy_id");
	     query.execute();

	     List<Object[]> result = query.getResultList();
	     List<ChartsDto> charts = new ArrayList<>();
	     for (Object[] row : result) {
	         ChartsDto chart = new ChartsDto(
	             (Number) row[0], // count
	             (String) row[1]  // positionName
	         );
	         charts.add(chart);
	     }
	     
	     return charts;
	 }
	 @Transactional()
	 public List<CandidateReportDto> fetchCandidatesByVacancyId(Integer vacancyId) {
	        StoredProcedureQuery query = em.createStoredProcedureQuery("sp_getCandidatesByVacancyId");
	        query.registerStoredProcedureParameter(1, Integer.class, ParameterMode.IN);
	        query.setParameter(1, vacancyId);

	        query.execute();

	        List<Object[]> result = query.getResultList();
	        List<CandidateReportDto> candidates = new ArrayList<>();
	        for (Object[] row : result) {
	        	CandidateReportDto candidate = new CandidateReportDto(
	        			 (Integer) row[0], // id
	        		        (String) row[1],  // name
	        		        ((java.sql.Date) row[2]).toLocalDate(), // date
	        		        (String) row[3],  // cv_status
	        		        (String) row[4],  // dob
	        		        (String) row[5],  // gender
	        		        (String) row[6],  // phone
	        		        (String) row[7],  // email
	        		        (String) row[8],  // degree
	        		        (String) row[9],  // technical
	        		        (String) row[10], // language
	        		        (String) row[11], // position
	        		        (String) row[12], // level
	        		        (String) row[13], // main_technical
	        		        (String) row[14], // experiences
	        		        (String) row[15],//salary
	        		        (String) row[16]//interview_date
	        		    );
	            candidates.add(candidate);
	        }
	    
	        return candidates;
	    }
	public List<MonthlyCountDto> getMonthlyCounts() {
		List<Object[]> results = candidateRepo.findMonthlyCounts();

		// Convert Object[] results into DTOs
		return results.stream()
				.map(result -> {
					Integer year = (result[0] instanceof Long) ? ((Long) result[0]).intValue() : (Integer) result[0];
					Integer month = (result[1] instanceof Long) ? ((Long) result[1]).intValue() : (Integer) result[1];
					Long count = (result[2] instanceof Integer) ? ((Integer) result[2]).longValue() : (Long) result[2];
					return new MonthlyCountDto(year, month, count);
				})
				.collect(Collectors.toList());
	}

	public List<Candidate> findCandidateCounts(Integer vacancyId, CvStatus status) {
		return candidateRepo.findByVacancyIdAndCvStatus(vacancyId,status);
	}
}