package com.blackjack.jraoms.controller;

import com.blackjack.jraoms.entity.Company;
import com.blackjack.jraoms.repository.CompanyRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@AllArgsConstructor
public class CompanyController {

    private final CompanyRepository companyRepository;

    @GetMapping("/company")
    @ResponseBody
    public List<Company> companyList(){
        return companyRepository.findAll();
    }
}