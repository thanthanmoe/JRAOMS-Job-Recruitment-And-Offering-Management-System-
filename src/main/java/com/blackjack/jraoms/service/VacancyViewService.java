package com.blackjack.jraoms.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.blackjack.jraoms.entity.Vacancy;
import com.blackjack.jraoms.repository.VacancyRepository;

@Service
@AllArgsConstructor
public class VacancyViewService {

	private final CandidateService candidateService;
	private final VacancyRepository vacancyRepository;

	public void calculateDaysLeftForVacancies(List<Vacancy> vacancies) {
		for (Vacancy vacancy : vacancies) {
			int count = candidateService.findAllCandidateByVacancyId(vacancy.getId()).size();
            int daysLeft = calculateDaysLeft(vacancy.getEndDate());
            vacancy.setApplyerAmount(count);
            vacancy.setDaysLeft(Math.abs(daysLeft));
		}
	}

	public int calculateDaysLeft(LocalDate endDate) {
		return (int) ChronoUnit.DAYS.between(LocalDate.now(), endDate);
	}
	 
	public String getOnlyActiveVacancies(Model model) {
		List<Vacancy> list = vacancyRepository.findBystatus("active");
		System.out.println("active vacancy : ");
		calculateDaysLeftForVacancies(list);
		model.addAttribute("vacancies", list);
		return "/junior/active-vacancy";
	}

	public String getAllExpiredAndClosedVacancy(Model model) {
		List<Vacancy> resultList = vacancyRepository.findByStatusIn(Arrays.asList("Expired", "close"));
	    calculateDaysLeftForVacancies(resultList);
		model.addAttribute("vacancies",resultList);
		return "/junior/expired-vacancy";
	}

}
