package com.blackjack.jraoms.controller;

import com.blackjack.jraoms.entity.Vacancy;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.blackjack.jraoms.service.VacancyService;
import com.blackjack.jraoms.service.VacancyViewService;

import java.util.List;

@Controller
@AllArgsConstructor
public class VacancyController {

    private final VacancyViewService vacancyViewService;
    @GetMapping("/activevacancies")
    public String getAllActiveVacancies(Model model) {
        return vacancyViewService.getOnlyActiveVacancies(model);
    }

    @GetMapping("/expiredvacancies")
    public String getExpiredVacancies(Model model) {
        return vacancyViewService.getAllExpiredAndClosedVacancy(model);
    }
}


