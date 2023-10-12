package com.blackjack.jraoms.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.blackjack.jraoms.dto.MonthlyCountDto;
import org.springframework.stereotype.Service;
import com.blackjack.jraoms.dto.VacancyDto;
import com.blackjack.jraoms.entity.User;
import com.blackjack.jraoms.entity.Vacancy;
import com.blackjack.jraoms.entity.VacancyHistory;
import com.blackjack.jraoms.repository.CandidateRepository;
import com.blackjack.jraoms.repository.DepartmentRepository;
import com.blackjack.jraoms.repository.VacancyRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class VacancyService {

	private final VacancyRepository repository;
	private final DepartmentRepository departmentRepository;
	private final CandidateRepository candidateRepository;
	private final VacancyHistoryService vacancyHistoryService;

	public void saveEntity(Vacancy vacancy) {
		repository.save(vacancy);
	}

	public void saveDto(VacancyDto dto, User user) {
		LocalDate currentDate = LocalDate.now();
		Vacancy vacancy;
		if (dto.getVacancyId() == 0) {
			vacancy = new Vacancy();
			vacancy.setStartDate(currentDate);
		} else {
			vacancy = repository.findById(dto.getVacancyId()).get();
		}

		vacancy.setPosition(dto.getPositionName());
		vacancy.setRequirePosition(Integer.valueOf(dto.getPosts()));
		vacancy.setWorkingDay(dto.getWorkingDays().toString());
		vacancy.setWorkingHours(dto.getWorkingHourFrom() + "-" + dto.getWorkingHourTo());
		vacancy.setSalary(dto.getSalary());
		vacancy.setJobType(dto.getJobType());
		vacancy.setRequirements(dto.getRequirements());
		vacancy.setResponsibilities(dto.getResponsibilities());
		vacancy.setDescription(dto.getDescription());
		vacancy.setPreferences(dto.getPreference());
		vacancy.setEndDate(currentDate.plusMonths(1));
		vacancy.setDepartment(departmentRepository.findById(dto.getDepartmentId()).orElse(null));
		vacancy.setStatus("active");
		repository.save(vacancy);

		VacancyHistory history = new VacancyHistory();
		history.setUser(user);
		history.setDate(dto.getStartDate());
		if (dto.getVacancyId() == 0) {
			history.setAction("create");
		} else {
			history.setAction("update");
		}
		history.setDate(currentDate);
		history.setVacancy(vacancy);
		vacancyHistoryService.save(history);

	}

	public Optional<Vacancy> findById(int id) {
		return repository.findById(id);
	}

	public Vacancy findByVacancy(int id) {
		return repository.findById(id).get();
	}

	public VacancyDto findVacancyDtoById(int id) {

		VacancyDto bean = new VacancyDto();
		Vacancy vacancy = repository.findById(id).get();
		bean.setVacancyId(vacancy.getId());
		bean.setPositionName(vacancy.getPosition());
		bean.setPosts(vacancy.getRequirePosition() + "");
		bean.setPreference(vacancy.getPreferences());
		bean.setRequirements(vacancy.getRequirements());
		bean.setDescription(vacancy.getDescription());
		bean.setResponsibilities(vacancy.getResponsibilities());
		bean.setSalary(vacancy.getSalary());

		List<String> restoredList = new ArrayList<String>();
		String[] stringArray = vacancy.getWorkingDay().substring(1, vacancy.getWorkingDay().length() - 1).split(", ");
		for (int i = 0; i < stringArray.length; i++) {
			stringArray[i] = stringArray[i].trim();
			restoredList.add(stringArray[i].trim());
		}
		bean.setWorkingDays(restoredList);
		String[] timeArr = vacancy.getWorkingHours().split("-");
		bean.setWorkingHourFrom(timeArr[0]);
		bean.setWorkingHourTo(timeArr[1]);
		bean.setJobType(vacancy.getJobType());
		bean.setCompanyId(vacancy.getDepartment().getCompany().getId());
		bean.setCompanyName(vacancy.getDepartment().getCompany().getName());
		bean.setDepartmentId(vacancy.getDepartment().getId());
		bean.setDepartmentName(vacancy.getDepartment().getName());
		return bean;
	}

	public int reopenVacancy(VacancyDto dto, User user) {
		int vacancyId = 0;
		LocalDate currentDate = LocalDate.now();

		Vacancy currentData = repository.findById(dto.getVacancyId()).get();
		   currentData.setRequirePosition(Integer.valueOf(dto.getPosts()));
		   currentData.setWorkingDay(dto.getWorkingDays().toString());
		   currentData.setWorkingHours(dto.getWorkingHourFrom() + "-" + dto.getWorkingHourTo());
		   currentData.setSalary(dto.getSalary());
		   currentData.setJobType(dto.getJobType());
	   	   currentData.setRequirements(dto.getRequirements());
		   currentData.setResponsibilities(dto.getResponsibilities());
		   currentData.setDescription(dto.getDescription());
		   currentData.setPreferences(dto.getPreference());
		   currentData.setDepartment(departmentRepository.findById(dto.getDepartmentId()).get());
		   currentData.setStartDate(currentDate);
		   currentData.setEndDate(currentDate.plusMonths(1));
		   currentData.setStatus("active");
			repository.save(currentData);

			VacancyHistory history = new VacancyHistory();
			history.setUser(user);
			history.setDate(dto.getStartDate());
			history.setAction("reopen");
			history.setDate(currentDate);
			history.setVacancy(currentData);
			vacancyHistoryService.save(history);
		 
		return vacancyId;
	}

	public List<Vacancy> findAllByStatus(String status) {
		List<Vacancy> list = repository.findBystatus(status);
		Iterator<Vacancy> it = list.iterator();

		while (it.hasNext()) {
			Vacancy vacancy = it.next();
			if (vacancy.getEndDate().isEqual(LocalDate.now()) || vacancy.getEndDate().isBefore(LocalDate.now())) {
				vacancy.setStatus("Expired");
				repository.save(vacancy);
			} 
			int dayBehind = calculateDays(vacancy.getStartDate());
			vacancy.setDaysLeft(dayBehind);
			 
		}
		
		return list;
	}

	private int calculateDays(LocalDate endDate) {
		return (int) ChronoUnit.DAYS.between(endDate, LocalDate.now());
	}

	public List<MonthlyCountDto> getMonthlyCounts() {
		List<Object[]> results = repository.findMonthlyCreateActions();

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

}