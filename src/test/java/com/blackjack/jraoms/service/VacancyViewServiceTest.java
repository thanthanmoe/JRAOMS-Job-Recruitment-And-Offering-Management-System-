package com.blackjack.jraoms.service;

import com.blackjack.jraoms.entity.Vacancy;
import com.blackjack.jraoms.repository.VacancyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VacancyViewServiceTest {

    @Mock CandidateService candidateService;
    @Mock VacancyRepository vacancyRepository;
    @InjectMocks VacancyViewService vacancyViewService;

    @Test
    public void testFilterActiveVacancies() {
        LocalDate today = LocalDate.now();

        Vacancy vacancy1 = mock(Vacancy.class);
        when(vacancy1.getEndDate()).thenReturn(LocalDate.now().minusDays(1));

        Vacancy vacancy2 = mock(Vacancy.class);
        when(vacancy2.getEndDate()).thenReturn(LocalDate.now());

        Vacancy vacancy3 = mock(Vacancy.class);
        when(vacancy3.getEndDate()).thenReturn(LocalDate.now().plusDays(1));

        List<Vacancy> vacancies = new ArrayList<>();
        vacancies.add(vacancy1);
        vacancies.add(vacancy2);
        vacancies.add(vacancy3);

        List<Vacancy> activeVacancies = filterActiveVacancies(vacancies);

        assertEquals(2, activeVacancies.size());
    }

    private List<Vacancy> filterActiveVacancies(List<Vacancy> vacancies) {
        LocalDate today = LocalDate.now();
        return vacancies.stream().filter(vacancy -> vacancy.getEndDate().compareTo(today) >= 0)
                .collect(Collectors.toList());
    }

    @Test
    public void testCalculateDaysLeftForVacancies() {

        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(10);
        Vacancy vacancy = new Vacancy();
        vacancy.setId(1);
        vacancy.setEndDate(endDate);
        List<Vacancy> vacancies = new ArrayList<>();
        vacancies.add(vacancy);
        when(candidateService.findAllCandidateByVacancyId(vacancy.getId())).thenReturn(new ArrayList<>());
        vacancyViewService.calculateDaysLeftForVacancies(vacancies);
        verify(candidateService).findAllCandidateByVacancyId(vacancy.getId());
        assertEquals(0, vacancy.getApplyerAmount());
        assertEquals(10, vacancy.getDaysLeft());
    }

    @Test
    void testGetOnlyActiveVacancies() {
        List<Vacancy> mockVacancies = new ArrayList<>();

        when(vacancyRepository.findBystatus("active")).thenReturn(mockVacancies);

        Model model = new ConcurrentModel();

        String result = vacancyViewService.getOnlyActiveVacancies(model);

        assertEquals("/junior/active-vacancy", result);
        verify(vacancyRepository, times(1)).findBystatus("active");
    }

    @Test
    void testGetAllExpiredAndClosedVacancy() {
        List<Vacancy> mockVacancies = new ArrayList<>();
        when(vacancyRepository.findByStatusIn(Arrays.asList("Expired", "close"))).thenReturn(mockVacancies);
        Model model = new ConcurrentModel();

        String result = vacancyViewService.getAllExpiredAndClosedVacancy(model);

        assertEquals("/junior/expired-vacancy", result);

        verify(vacancyRepository, times(1)).findByStatusIn(Arrays.asList("Expired", "close"));
    }
    /*
    @Test
    void testFilterActiveAndExpiredVacancy() {
        List<Vacancy> mockVacancies = new ArrayList<>(); // Create some mock Vacancy objects

        // Set up mock behavior for vacancyRepository.findById
        when(vacancyRepository.findById(anyInt())).thenReturn(Optional.of(new Vacancy()));

        LocalDate currentDate = LocalDate.now();

        // Simulate some mock Vacancy objects
        Vacancy activeVacancy = new Vacancy();
        activeVacancy.setId(1);
        activeVacancy.setStatus("active");
        activeVacancy.setEndDate(currentDate.minusDays(1)); // Expired

        Vacancy openVacancy = new Vacancy();
        openVacancy.setId(2);
        openVacancy.setStatus("open");
        openVacancy.setEndDate(currentDate.plusDays(1)); // Active

        mockVacancies.add(activeVacancy);
        mockVacancies.add(openVacancy);

        // Simulate the list passed to the method
        List<Vacancy> inputList = new ArrayList<>(mockVacancies);

        vacancyViewService.filterActiveAndExpiredVacancy(inputList);

        // Verify that findById is called for each vacancy
        verify(vacancyRepository, times(mockVacancies.size())).findById(anyInt());

        // Verify that vacancyRepository.save is called for each updated vacancy
        verify(vacancyRepository, times(2)).save(any());

        // Verify the changes in the status of vacancies
        assertEquals("Expired", activeVacancy.getStatus());
        assertEquals("active", openVacancy.getStatus());
    }*/
}