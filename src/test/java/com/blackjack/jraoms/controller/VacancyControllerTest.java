package com.blackjack.jraoms.controller;

import com.blackjack.jraoms.service.VacancyViewService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VacancyControllerTest {
    @Mock private VacancyViewService vacancyViewService;
    @Mock private Model model;
    @InjectMocks private VacancyController vacancyController;

    @Test
    void testGetAllActiveVacancies() {
        when(vacancyViewService.getOnlyActiveVacancies(model)).thenReturn("activeVacanciesPage");

        String result = vacancyController.getAllActiveVacancies(model);

        verify(vacancyViewService).getOnlyActiveVacancies(model);
        verifyNoMoreInteractions(vacancyViewService);
    }

    @Test
    void testGetExpiredVacancies() {
        when(vacancyViewService.getAllExpiredAndClosedVacancy(model)).thenReturn("expiredVacanciesPage");

        String result = vacancyController.getExpiredVacancies(model);

        verify(vacancyViewService).getAllExpiredAndClosedVacancy(model);
        verifyNoMoreInteractions(vacancyViewService);
    }
}