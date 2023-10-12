package com.blackjack.jraoms.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blackjack.jraoms.entity.VacancyHistory;
import com.blackjack.jraoms.repository.VacancyHistoryRepository;

@Service
public class VacancyHistoryService {

	@Autowired
	private VacancyHistoryRepository vacancyHistoryRepo;
	
	public void save(VacancyHistory entity) {
		vacancyHistoryRepo.save(entity);
	}
	
	public List<VacancyHistory> findByVacancyId(Integer id) {
		return calculateDaysList(vacancyHistoryRepo.findByVacancy_id(id));
	}
	
	public List<VacancyHistory> calculateDaysList(List<VacancyHistory> list){
		Iterator<VacancyHistory> it = list.iterator();
		while(it.hasNext()) {
			VacancyHistory history = it.next();
			int dayBehind = calculateDays(history.getDate());
			if(dayBehind == 0) {
			history.setDayBehind("Today");
			}else {
			history.setDayBehind(dayBehind+" days Ago");
			}
		}
		return list;
	}
	 public int calculateDays(LocalDate endDate) {
			return (int) ChronoUnit.DAYS.between(endDate,LocalDate.now());
		}

	public List<Object[]> getMonthlyCreateActions() {
		return vacancyHistoryRepo.findMonthlyCreateActions();
	}
}
