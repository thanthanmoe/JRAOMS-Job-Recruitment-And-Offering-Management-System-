// Java Program to Illustrate Scheduling Task
// using a cron expression

package com.blackjack.jraoms;

// Importing required classes
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.blackjack.jraoms.entity.Vacancy;
import com.blackjack.jraoms.repository.VacancyRepository;
import com.blackjack.jraoms.service.VacancyService;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;

// Annotation
@Component
// Class
public class Scheduler {

	@Autowired
	VacancyService vacancyService;
	@Autowired
	VacancyRepository repo;

	
	@PostConstruct
    public void init() {
        scheduleTask();
    }
	
	@Transactional
	@Scheduled(cron = "0 0 0 * * *")
	public void scheduleTask() {
        System.err.print("worked");
        List<Vacancy> list = repo.findByStatusIn(Arrays.asList("Expired", "active"));
		Iterator<Vacancy> it = list.iterator();
		while (it.hasNext()) {
			Vacancy vacancy = it.next();
			if (vacancy.getEndDate().isEqual(LocalDate.now()) || LocalDate.now().isAfter(vacancy.getEndDate())) {
				Vacancy entity = vacancyService.findByVacancy(vacancy.getId());
				entity.setStatus("Expired");
				vacancyService.saveEntity(entity);
				System.out.println("Vacancy_"+vacancy.getPosition()+"_setTo_Expired");
			} else {
				Vacancy entity = vacancyService.findByVacancy(vacancy.getId());
				entity.setStatus("active");
				vacancyService.saveEntity(entity);
				System.out.println("Vacancy_"+vacancy.getPosition()+"_setTo_Active");
			}
		}
	}

	private LocalDate lastCheckedTime = LocalDate.now();

	@Scheduled(fixedRate = 60000) // Runs every 60 seconds
	public void checkSystemTimeChange() {
		LocalDate currentSystemTime = LocalDate.now();

		if (!currentSystemTime.isEqual(lastCheckedTime)) {
			scheduleTask();
			lastCheckedTime = currentSystemTime;
		}
	}

}
