package com.blackjack.jraoms.service;

import com.blackjack.jraoms.entity.VacancyHistory;
import com.blackjack.jraoms.repository.VacancyHistoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class VacancyHistoryServiceTest {

    @InjectMocks
    private VacancyHistoryService vacancyHistoryService;
    @Mock
    private VacancyHistoryRepository vacancyHistoryRepo;

    @Test
    public void testSaveVacancyHistory() {
        VacancyHistory vacancyHistory = new VacancyHistory();
        vacancyHistoryService.save(vacancyHistory);
        verify(vacancyHistoryRepo).save(vacancyHistory);
    }

    @Test
    void testCalculateDaysList() {
        // Mock the VacancyHistory objects
        VacancyHistory history1 = mock(VacancyHistory.class);
        when(history1.getDate()).thenReturn(LocalDate.now()); // Today's date
        VacancyHistory history2 = mock(VacancyHistory.class);
        when(history2.getDate()).thenReturn(LocalDate.now().minusDays(2)); // 2 days ago
        VacancyHistory history3 = mock(VacancyHistory.class);
        when(history3.getDate()).thenReturn(LocalDate.now().minusDays(5)); // 5 days ago

        List<VacancyHistory> historyList = new ArrayList<>();
        historyList.add(history1);
        historyList.add(history2);
        historyList.add(history3);

        VacancyHistoryService calculator = new VacancyHistoryService(); // Create an instance of your class
        List<VacancyHistory> result = calculator.calculateDaysList(historyList);

        // Verify the expected behavior
        verify(history1, Mockito.times(1)).setDayBehind("Today");
        verify(history2, Mockito.times(1)).setDayBehind("2 days Ago");
        verify(history3, Mockito.times(1)).setDayBehind("5 days Ago");

        // Assert the size of the result list
        assertEquals(3, result.size());
    }

    @Test
    void testFindByVacancyId() {
        // Mock the VacancyHistoryRepo

        // Create sample vacancy histories
        VacancyHistory history1 = new VacancyHistory();
        history1.setDate(LocalDate.now());
        VacancyHistory history2 = new VacancyHistory();
        history2.setDate(LocalDate.now().minusDays(2));
        VacancyHistory history3 = new VacancyHistory();
        history3.setDate(LocalDate.now().minusDays(5));
        List<VacancyHistory> historyList = new ArrayList<>();
        historyList.add(history1);
        historyList.add(history2);
        historyList.add(history3);

        // Mock the repository's behavior
        when(vacancyHistoryRepo.findByVacancy_id(1)).thenReturn(historyList);

        // Call the method
        List<VacancyHistory> result = vacancyHistoryService.findByVacancyId(1);

        // Verify that the repository method was called
        verify(vacancyHistoryRepo, Mockito.times(1)).findByVacancy_id(1);

        // Verify the expected behavior
        assertEquals(3, result.size());
        assertEquals("Today", result.get(0).getDayBehind());
        assertEquals("2 days Ago", result.get(1).getDayBehind());
        assertEquals("5 days Ago", result.get(2).getDayBehind());
    }

    @Test
    void testCalculateDays() {
        // Create an instance of your class
        VacancyHistoryService historyService = new VacancyHistoryService();

        // Calculate days using the private method
        LocalDate endDate = LocalDate.now().minusDays(3); // 3 days ago
        int days = historyService.calculateDays(endDate);

        // Verify the calculated days
        assertEquals(3, days);
    }

    @Test
    void testGetMonthlyCreateActions() {

        Object[] data1 = new Object[]{"January", 5}; // Month and count
        Object[] data2 = new Object[]{"February", 8};
        List<Object[]> dataList = new ArrayList<>();
        dataList.add(data1);
        dataList.add(data2);

        // Mock the repository's behavior
        when(vacancyHistoryRepo.findMonthlyCreateActions()).thenReturn(dataList);

        // Call the method
        List<Object[]> result = vacancyHistoryService.getMonthlyCreateActions();

        // Verify that the repository method was called
        verify(vacancyHistoryRepo, Mockito.times(1)).findMonthlyCreateActions();

        // Verify the expected behavior
        assertEquals(2, result.size());
        assertArrayEquals(data1, result.get(0));
        assertArrayEquals(data2, result.get(1));
    }
    @Test
    public void testSave() {
        VacancyHistory history = new VacancyHistory();
        vacancyHistoryService.save(history);

        verify(vacancyHistoryRepo, times(1)).save(history);
    }



}