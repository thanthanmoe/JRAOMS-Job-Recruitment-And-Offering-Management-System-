package com.blackjack.jraoms.controller;

import com.blackjack.jraoms.dto.MonthlyCountDto;
import com.blackjack.jraoms.service.CandidateService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@AllArgsConstructor
public class PageController {
private CandidateService candidateService;
    @GetMapping("/dashboard")
    public String dashboard(ModelMap model){

        List<MonthlyCountDto> chart = candidateService.getMonthlyCounts();
        model.addAttribute("candidateData", chart);
        return "/dashboard/dashboard";
    }
}
